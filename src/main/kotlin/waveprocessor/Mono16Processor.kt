package waveprocessor

import com.google.common.io.LittleEndianDataInputStream
import com.google.common.io.LittleEndianDataOutputStream
import java.io.*

data class Mono16Wave(
    val samplesPerSec: Int,
    val bitsPerSample: Short,
    val size: Int,
    val data: List<Short>,
)

class Mono16Processor {
    private val blockSize
        get() = 2

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
                val data = List<Short>(size = size / blockSize) { idx ->
                    readShort()
                }
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
                writeShort(1)
                writeShort(1)
                writeInt(wave.samplesPerSec)
                writeInt(wave.samplesPerSec * blockSize)
                writeShort(blockSize)
                writeShort(wave.bitsPerSample.toInt())
                write("data".toByteArray())
                writeInt(wave.size)
                wave.data.forEach {
                    writeShort(it.toInt())
                }
            }
            return this
        } catch (e: Exception) {
            println(e.message)
            return null
        }
    }
}
