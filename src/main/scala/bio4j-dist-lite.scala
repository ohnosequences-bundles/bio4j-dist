package ohnosequences.statika

import ohnosequences.statika._, bundles._, instructions._
import ohnosequences.awstools.s3._

import com.amazonaws.auth._ //, profile._
import com.amazonaws.services.s3._
import com.amazonaws.services.s3.model._

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

  val dbDistObject = ObjectAddress(s"${region}.releases.bio4j.com", s"2014_12_03/${name}")
  val dbLocation = new File(s"/media/ephemeral0/${name}")

  def instructions: AnyInstructions = {

    LazyTry {
      // val s3 = S3.create(new InstanceProfileCredentialsProvider()) // we rely on instance role credentials
      // val s3 = S3.create(new ProfileCredentialsProvider("default")) // we rely on instance role credentials
      // val s3 = new AmazonS3Client(new ProfileCredentialsProvider("default")) // we rely on instance role credentials
      val s3 = new AmazonS3Client(new InstanceProfileCredentialsProvider()) // we rely on instance role credentials
      val request = new GetObjectRequest(dbDistObject.bucket, dbDistObject.key, true)

      // if (!dbLocation.exists) dbLocation.mkdirs

      println(s"Dowloading from: ${dbDistObject}")
      println(s"File: ${dbLocation.getAbsolutePath}")

      // val loader = s3.createLoadingManager
      // val download = loader.transferManager.download(request, dbLocation)
      // loader.transferWaiter(download)
      s3.getObject(request, dbLocation)
    } -&-
    say(s"Distribution ${bundleName} was dowloaded to ${dbLocation}")

  }
}


// case object bio4jLite extends Bio4jDist("bio4j_all_but_uniref_and_gi_index.tar")


case object bio4jTaxonomy extends Bio4jDist("taxonomy") {

  override val dbDistObject = ObjectAddress(s"${region}.resources.ohnosequences.com", "16s/bio4j")
}
