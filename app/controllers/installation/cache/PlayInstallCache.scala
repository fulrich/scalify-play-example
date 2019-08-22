package controllers.installation.cache

import javax.inject.{Inject, Singleton}
import play.api.cache.SyncCacheApi


@Singleton
class PlayInstallCache @Inject()(cache: SyncCacheApi) extends InstallCache {
  val NonceTag = "nonce"

  private def nonceId(shop: String): String = s"$shop-$NonceTag"

  def getNonce(shop: String): Option[String] = cache.get[String](nonceId(shop))
  def setNonce(shop: String, nonce:String): Unit = cache.set(nonceId(shop), nonce)
}
