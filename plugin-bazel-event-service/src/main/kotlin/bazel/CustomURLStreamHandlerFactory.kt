package bazel

import java.net.URLStreamHandler
import java.net.URLStreamHandlerFactory

class CustomURLStreamHandlerFactory(val remoteCache: String?) : URLStreamHandlerFactory {
    override fun createURLStreamHandler(protocol: String): URLStreamHandler? {
        return when(protocol.toLowerCase()) {
            "bytestream" -> BytestreamURLStreamHandler(remoteCache)
            else -> null
        }
    }
}