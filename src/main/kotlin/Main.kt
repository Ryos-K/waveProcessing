import waveprocessor.Mono16Processor

fun main(args: Array<String>) {
	val waveProcessor = Mono16Processor()
	waveProcessor
		.read("src/main/resources/guitar_A4.wav")
		?.showInfo()
		?.write("src/main/resources/hoge.wav")
}