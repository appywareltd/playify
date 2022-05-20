package com.kaya.playify.playify

import android.content.Context
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** PlayifyPlugin */
class PlayifyPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var applicationContext: Context? = null

    private var playifyPlayer = PlayifyPlayer()

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        applicationContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.kaya.playify/playify")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == "getAllSongs") {
            val songs = applicationContext?.let {
                playifyPlayer.getAllSongs(it)
            }

            result.success(
                songs?.map {
                    it.toMap()
                }?.toList()
            )
        } else if (call.method == "playItem") {
            val id = call.argument<String>("songID")

            id?.let { id ->
                applicationContext?.let {
                    val res = playifyPlayer.playItem(context = it, id = id)
                    result.success(res)
                }
            } ?: run {
                result.error("ArgumentError", "Argument id was not provided!", null)
            }
        } else if (call.method == "getVolume") {
            applicationContext?.let {
                val vol = playifyPlayer.getVolume(it)
                result.success(vol)
            }
        } else if (call.method == "setVolume") {
            val amount = call.argument<Double>("volume")

            amount?.let { volumeAmount ->
                applicationContext?.let {
                    val vol = playifyPlayer.setVolume(it, volumeAmount)
                    result.success(vol)
                }
            } ?: run {
                result.error("ArgumentError", "Argument volume was not provided!", null)
            }
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}
