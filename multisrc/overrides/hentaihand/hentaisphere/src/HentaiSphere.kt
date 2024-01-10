package eu.kanade.tachiyomi.revived.en.hentaisphere

import eu.kanade.tachiyomi.multisrc.hentaihand.HentaiHand
import okhttp3.OkHttpClient

class HentaiSphere : HentaiHand("HentaiSphere", "https://hentaisphere.com", "en", false) {
    override val client: OkHttpClient = network.cloudflareClient.newBuilder()
        .addInterceptor { authIntercept(it) }
        .build()
}
