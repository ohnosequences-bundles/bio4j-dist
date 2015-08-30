package ohnosequences.statika

import ohnosequences.statika._, bundles._, instructions._
import ohnosequences.awstools.s3._

import com.amazonaws.auth._, profile._
import com.amazonaws.services.s3._
import com.amazonaws.services.s3.model._
import com.amazonaws.services.s3.transfer._

import java.io.File
import sys.process._


case class MountSSD(device: String) extends Bundle() {

  def instructions: AnyInstructions = {
    cmd("mkfs")("-t", "ext4", s"/dev/${device}") -&-
    cmd("mount")(s"/dev/${device}") -&-
    say(s"Device ${device} is mounted")
  }
}


abstract class Bio4jDist(name: String) extends Bundle(MountSSD("xvdb")) {
  val region = "eu-west-1"

  val dbDistObject: ObjectAddress
  // = ObjectAddress(s"${region}.releases.bio4j.com", s"2014_12_03/${name}")
  val dbLocation = new File(s"/media/ephemeral0/")

  def instructions: AnyInstructions = {

    LazyTry {
      val transferManager = new TransferManager(new InstanceProfileCredentialsProvider())
      // val transferManager = new TransferManager(new ProfileCredentialsProvider("default"))

      if (!dbLocation.exists) dbLocation.mkdirs

      println(s"Dowloading from: ${dbDistObject}")
      println(s"File: ${dbLocation.getAbsolutePath}")

      val transfer = transferManager.downloadDirectory(dbDistObject.bucket, dbDistObject.key, dbLocation)
      transfer.waitForCompletion
    } -&-
    say(s"Distribution ${bundleName} was dowloaded to ${dbLocation}")

  }
}


// case object bio4jLite extends Bio4jDist("bio4j_all_but_uniref_and_gi_index.tar")


case object bio4jTaxonomy extends Bio4jDist("taxonomy") {

  val dbDistObject = ObjectAddress("resources.ohnosequences.com", "16s/bio4j")
}
