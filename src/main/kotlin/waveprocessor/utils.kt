package waveprocessor

import kotlin.math.sqrt

data class Complex(
	val re: Double,
	val im: Double,
) {
	infix operator fun plus(c: Complex) = Complex(re + c.re, im + c.im)
	infix operator fun minus(c: Complex) = Complex(re - c.re, im - c.im)
	infix operator fun times(d: Double)  = Complex(re * d, im * d)
	infix operator fun times(c: Complex) = Complex(re * c.re - im * c.im, re * c.im + im * c.re)
	infix operator fun div(d: Double) = Complex(re / d, im / d)
	infix operator fun div(c: Complex) = this * c.conjugate() / c.norm2()
	fun conjugate() = Complex(re, -im)
	fun norm() = sqrt(re * re + im * im)
	fun norm2() = re * re + im * im
}

fun fft(x: Array<Complex>) {

}