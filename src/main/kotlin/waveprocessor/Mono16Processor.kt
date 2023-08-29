package waveprocessor

import com.google.common.io.LittleEndianDataInputStream
import com.google.common.io.LittleEndianDataOutputStream
import java.awt.image.SampleModel
import java.io.*

data class Mono16Wave(
	val samplesPerSec: Int,
	val bitsPerSample: Short,
	val size: Int,
	val data: List<Short>,
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
				val data = List(size / blockSize) { readShort() }
				wave = Mono16Wave(
					samplesPerSec, bitsPerSample, size, data
				)
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
				wave.data.forEach { writeShort(it.toInt()) }
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
			DataSize      : ${wave.data.size}
            	""".trimIndent()
			)
		} else {
			println("wave is not initialized")
		}
		return this
	}
}
