package waveprocessor

import kotlin.math.*

data class Complex(
	val re: Double,
	val im: Double,
) {
	infix operator fun plus(c: Complex) = Complex(re + c.re, im + c.im)
	infix operator fun minus(c: Complex) = Complex(re - c.re, im - c.im)
	infix operator fun times(d: Double) = Complex(re * d, im * d)
	infix operator fun times(c: Complex) = Complex(re * c.re - im * c.im, re * c.im + im * c.re)
	infix operator fun div(d: Double) = Complex(re / d, im / d)
	infix operator fun div(c: Complex) = this * c.conjugate() / c.norm2()
	fun conjugate() = Complex(re, -im)
	fun norm() = sqrt(re * re + im * im)
	fun norm2() = re * re + im * im
}

fun Number.toComplex() = Complex(this.toDouble(), 0.0)

//fun fft(x: Array<Complex>, exp: Int): Array<Complex> {
//	val X = Array(2.0.pow(exp).toInt()) { Complex(.0, .0) }
//
//	val n = 2.0.pow(exp).toInt()
//}

fun sinc(x: Double) = if (x == .0) 1.0 else sin(x) / x

fun genHanningWindow(N: Int) = List(N) { 0.5 - 0.5 * cos(2.0 * PI * (it + 0.5) / N) }

fun genFirLpf(fe: Double, j: Int, window: List<Double>) = List(j + 1) {
	2.0 * fe * sinc(2.0 * PI * fe * it) * window[it]
}