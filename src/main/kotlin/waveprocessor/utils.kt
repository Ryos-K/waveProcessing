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

fun dft(x: List<Complex>, offset: Int = 0, n: Int = x.size - offset): List<Complex> {
	val wToPowerOf = List(n) { Complex(cos(2.0 * PI * it / n), -sin(2.0 * PI * it / n)) }
	val window = genHanningWindow(n)
	val windowedX = List(n) { x[offset + it] * window[it] }
	return List(n) { idx ->
		windowedX.foldIndexed(Complex(.0, .0)) { jdx, sum, current ->
			sum + wToPowerOf[(idx * jdx) % n] * current
		}
	}
}

//fun fft(x: Array<Complex>, exp: Int): Array<Complex> {
//	val X = Array(2.0.pow(exp).toInt()) { Complex(.0, .0) }
//
//	val n = 2.0.pow(exp).toInt()
//}

fun sinc(x: Double) = if (x == .0) 1.0 else sin(x) / x

fun genHanningWindow(n: Int) =
	if (n % 2 == 0) List(n) { 0.5 - 0.5 * cos(2.0 * PI * it / n) }
	else List(n) { 0.5 - 0.5 * cos(2 * PI * (it + 0.5) / n) }

fun genFirLpf(fe: Double, j: Int, window: List<Double>) = List(j + 1) {
	2.0 * fe * sinc(2.0 * PI * fe * (it - j / 2)) * window[it]
}