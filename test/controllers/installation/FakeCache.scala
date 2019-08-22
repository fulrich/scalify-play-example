package controllers.installation

import controllers.installation.cache.InstallCache

class FakeCache extends InstallCache {
  var nonceHolder: Option[String] = None

  override def setNonce(shop: String, nonce: String): Unit = nonceHolder = Some(nonce)
  override def getNonce(shop: String): Option[String] = nonceHolder
}
