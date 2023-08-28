import waveprocessor.Mono16Processor

fun main(args: Array<String>) {
    println("Hello World!")

    val waveProcessor = Mono16Processor()
    waveProcessor
        .read("src/main/resources/guitar_A4.wav")
        ?.write("src/main/resources/hoge.wav")
}