package controllers.installation.cache

import com.google.inject.ImplementedBy


@ImplementedBy(classOf[PlayInstallCache])
trait InstallCache {
  def getNonce(shop: String): Option[String]
  def setNonce(shop: String, nonce:String): Unit
}
