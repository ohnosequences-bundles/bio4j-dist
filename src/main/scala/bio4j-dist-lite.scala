package ohnosequences.statika

import ohnosequences.statika._, bundles._, instructions._
import ohnosequences.awstools.s3._

import com.amazonaws.auth._
import com.amazonaws.services.s3._
import com.amazonaws.services.s3.model._

import java.io.File


case object Bio4jDistLite extends Bundle() {
  val region = "eu-west-1"
  val dbDistObject = ObjectAddress(s"${region}.releases.bio4j.com", "2014_12_03/bio4j_lite.tar")
  val dbLocation = new File("/media/ephemeral0/bio4jtitandb-lite")

  // lazy val bio4jManager: Bio4jManager = new Bio4jManager(dbLocation.getAbsolutePath)
  // lazy val nodeRetriever: NodeRetrieverTitan = new NodeRetrieverTitan(bio4jManager)

  def install: Results = {

    try {
      val s3 = S3.create(new InstanceProfileCredentialsProvider()) // we rely on instance role credentials
      val request = new GetObjectRequest(dbDistObject.bucket, dbDistObject.key, true)

      if (!dbLocation.exists) dbLocation.mkdirs

      println(s"Dowloading from: ${dbDistObject}")
      println(s"File: ${dbLocation.getAbsolutePath}")

      val loader = s3.createLoadingManager
      val download = loader.transferManager.download(request, dbLocation)
      loader.transferWaiter(download)

      // InitBio4jTitan.main(Array(dbLocation.getAbsolutePath))
      success(s"Distribution ${bundleName} was dowloaded to ${dbLocation}")
    } catch {
      case e: Exception => failure(e.toString)
    }
  }

}
