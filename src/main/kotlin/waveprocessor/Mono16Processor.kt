package waveprocessor

import com.google.common.io.LittleEndianDataInputStream
import com.google.common.io.LittleEndianDataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

data class Mono16Wave(
	val samplesPerSec: Int,
	val bitsPerSample: Short,
	val size: Int, // "size" is "blockSize" times as long as "data.size"
	val data: List<Double>,
)

class Mono16Processor {
	private val waveFormatType = 1
	private val channel = 1
	private val blockSize = 2

	private var wave = Mono16Wave(0, 0, 0, listOf())

	fun read(fileName: String): Mono16Processor? {
		try {
			LittleEndianDataInputStream(FileInputStream(fileName)).run {
				skipNBytes(24)
				val samplesPerSec = readInt()
				skipNBytes(6)
				val bitsPerSample = readShort()
				skipNBytes(4)
				val size = readInt()
				val data = List(size / blockSize) { 1.0 * readShort() / Short.MAX_VALUE }
				wave = Mono16Wave(samplesPerSec, bitsPerSample, size, data)
			}
			return this
		} catch (e: Exception) {
			println(e.message)
			return null
		}
	}

	fun write(fileName: String): Mono16Processor? {
		clip(1.0)
		try {
			LittleEndianDataOutputStream(FileOutputStream(File(fileName), false)).run {
				write("RIFF".toByteArray())
				writeInt(wave.size + 36)
				write("WAVE".toByteArray())
				write("fmt ".toByteArray())
				writeInt(16)
				writeShort(waveFormatType)
				writeShort(channel)
				writeInt(wave.samplesPerSec)
				writeInt(wave.samplesPerSec * blockSize) // bytesPerSec
				writeShort(blockSize)
				writeShort(wave.bitsPerSample.toInt())
				write("data".toByteArray())
				writeInt(wave.size)
				wave.data.forEach { writeShort((it * Short.MAX_VALUE).toInt()) }
			}
			return this
		} catch (e: Exception) {
			println(e.message)
			return null
		}
	}

	fun showInfo(showData: Boolean = false): Mono16Processor {
		println(
			"""
			WaveFormatType: $waveFormatType
			channel		  : $channel
			SamplePerSec  : ${wave.samplesPerSec}
			BytesPerSec   : ${wave.samplesPerSec * blockSize}
			BlockSize     : ${blockSize}
			BitsPerSample : ${wave.bitsPerSample}
			Size          : ${wave.size}
			DataSize      : ${wave.data.size}
            	""".trimIndent()
		)
		if (showData) println(
			"""
			Data          :
			${wave.data}
			""".trimIndent()
		)
		return this
	}

	fun generateWave(samplesPerSec: Int, bitsPerSample: Short, size: Int, function: (Int) -> Double): Mono16Processor {
		wave = Mono16Wave(samplesPerSec, bitsPerSample, size * blockSize, List(size) { function(it) })
		return this
	}

	fun delay(weights: List<Double>, delayMillis: Int): Mono16Processor {
		val delaySamples = wave.samplesPerSec * delayMillis / 1000
		val data = MutableList(wave.data.size) { 0.0 }

		repeat(data.size) { idx ->
			data[idx] += wave.data[idx]
			weights.forEachIndexed { jdx, weight ->
				if (idx + (jdx + 1) * delaySamples < data.size)
					data[idx + (jdx + 1) * delaySamples] += wave.data[idx] * weight
			}
		}
		wave = wave.copy(data = data)
		return this
	}

	fun clip(level: Double): Mono16Processor {
		return clip(-level, level)
	}

	fun clip(lowerLevel: Double, upperLevel: Double): Mono16Processor {
		wave = wave.copy(data = wave.data.map {
			when {
				it > upperLevel -> upperLevel
				it < lowerLevel -> lowerLevel
				else -> it
			}
		})
		return this
	}

	fun distort(level: Double, amp: Double): Mono16Processor {
		wave = wave.copy(data = wave.data.map {
			when {
				it * amp > level -> level
				it * amp < -level -> -level
				else -> it * amp
			}
		})
		return this
	}

	fun compress(threshold: Double, ratio: Double): Mono16Processor {
		wave = wave.copy(data = wave.data.map {
			when {
				it > threshold -> threshold + (it - threshold) * ratio
				it < threshold -> -threshold + (it + threshold) * ratio
				else -> it
			}
		}.map {
			it / (threshold + (1 - threshold) * ratio)
		})
		return this
	}
}
