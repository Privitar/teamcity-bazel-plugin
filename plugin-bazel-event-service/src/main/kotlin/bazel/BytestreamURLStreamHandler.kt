package bazel

import java.net.URL
import java.net.URLConnection
import java.net.URLStreamHandler

class BytestreamURLStreamHandler(val remoteCache: String?): URLStreamHandler() {
    override fun openConnection(url: URL): URLConnection = BytestreamURLConnection(url, remoteCache)
}