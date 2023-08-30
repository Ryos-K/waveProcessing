import waveprocessor.Mono16Processor
import kotlin.math.PI
import kotlin.math.sin

fun main(args: Array<String>) {
	val A = 0.1
	val f0 = 440.0

	val waveProcessor = Mono16Processor()
	waveProcessor
		.read("src/main/resources/guitar_A4.wav")
//		.generateWave(8000, 16, 40000) { DoubleArray(4) { h -> A * sin(2.0 * PI * f0 * h * it / 8000) }.sum() }
//		?.delay(listOf(0.5, 0.2, 0.2, 0.2, 0.2), 100)
		?.distort(1.0, 20.0)
		?.clip(0.1, 0.9)
		?.showInfo()
		?.write("src/main/resources/hoge.wav")
}