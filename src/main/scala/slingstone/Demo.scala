package slingstone

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

/**
 * @author xuji
 */

case class MatchTypeConfig(name: String, candidateNum: Int, usePrediction: Boolean)
case class BlenderConfig(useDedup: Boolean, useVariety: Boolean)
case class FederationConfig(matchTypes: Seq[MatchTypeConfig])
case class BucketConfig(name: String, federationConfig: FederationConfig, blenderConfig: BlenderConfig)

object Demo extends App {
  println(createBucketConfigFromFile("demo.conf"))
  println(createBucketConfigFromFile("inherit.conf"))
  println(createBucketConfigFromFile("inherit_again.conf"))

  def createBucketConfigFromFile(fileName: String): BucketConfig = {
    val conf = ConfigFactory.parseResources(fileName)
    val blenderConfig = BlenderConfig(conf.getBoolean("blender.useDedup"), conf.getBoolean("blender.useVariety"))
    val matchTypeConfigs = conf.getConfigList("federation.matchTypes") map { config =>
      MatchTypeConfig(config.getString("name"), config.getInt("candidateNum"), config.getBoolean("usePrediction"))
    }
    val federationConfig = FederationConfig(matchTypeConfigs)
    BucketConfig(conf.getString("name"), federationConfig, blenderConfig)
  }
}
