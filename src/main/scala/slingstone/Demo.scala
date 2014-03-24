package slingstone

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConversions._

/**
 * @author xuji
 */

case class MatchTypeConfig(name: String, candidateNum: Int, usePrediction: Boolean)
case class UpServiceConfig(name: String, keys: String)

case class FederationConfig(predictionModel: String, predictionDocLimit: Int, matchTypes: Seq[MatchTypeConfig])
case class UserProfileConfig(upServices: Seq[UpServiceConfig])
case class BucketConfig(name: String, ccode: String, federationConfig: FederationConfig, userProfileConfig: UserProfileConfig)

object Demo extends App {
  println(createBucketConfigFromFile("base.conf"))
  println(createBucketConfigFromFile("changeModel.conf"))
  println(createBucketConfigFromFile("overrideMatchTypes.conf"))
  println(createBucketConfigFromFile("overrideUpServices.conf"))

  def createBucketConfigFromFile(fileName: String): BucketConfig = {
    val conf = ConfigFactory.parseResources(fileName)

    val name = conf.getString("name")
    val ccode = conf.getString("ccode")

    val matchTypeConfigs = conf.getConfigList("federation.matchTypes") map { config =>
      MatchTypeConfig(config.getString("name"), config.getInt("candidateNum"), config.getBoolean("usePrediction"))
    }
    val predictionModel = conf.getString("federation.predictionModel")
    val predictionDocLimit = conf.getInt("federation.predictionDocLimit")
    val federationConfig = FederationConfig(predictionModel, predictionDocLimit, matchTypeConfigs)

    val upServiceConfigs = conf.getConfigList("userprofile.upServices") map { config =>
      UpServiceConfig(config.getString("name"), config.getString("keys"))
    }
    val userProfileConfig = UserProfileConfig(upServiceConfigs)

    BucketConfig(name, ccode, federationConfig, userProfileConfig)
  }
}
