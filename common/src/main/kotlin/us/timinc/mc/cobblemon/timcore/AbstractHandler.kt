package us.timinc.mc.cobblemon.timcore

abstract class AbstractHandler<T> {
    abstract fun handle(evt: T)
}