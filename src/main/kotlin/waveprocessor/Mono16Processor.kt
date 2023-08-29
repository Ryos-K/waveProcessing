package waveprocessor

import com.google.common.io.LittleEndianDataInputStream
import com.google.common.io.LittleEndianDataOutputStream
import java.awt.image.SampleModel
import java.io.*
import kotlin.math.roundToInt

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

	private lateinit var wave: Mono16Wave

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

	fun showInfo(): Mono16Processor {
		if (this::wave.isInitialized) {
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
			Data          :
			${wave.data}
            	""".trimIndent()
			)
		} else {
			println("wave is not initialized")
		}
		return this
	}

	fun generateWave(samplesPerSec: Int, bitsPerSample: Short, size: Int, function: (Int) -> Double): Mono16Processor {
		wave = Mono16Wave(samplesPerSec, bitsPerSample, size * blockSize, List(size) { function(it) })
		return this
	}
}
