package ohnosequencesBundles.statika

import ohnosequences.statika._, bundles._, instructions._
import ohnosequences.awstools._, s3._, regions._

import com.amazonaws.auth._
import com.amazonaws.services.s3.transfer._

import java.io.File

import com.thinkaurelius.titan.core._
import com.bio4j.titan.model.ncbiTaxonomy._
import com.bio4j.titan.util.DefaultTitanGraph
import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.BaseConfiguration


trait AnyBio4jDist extends Bundle() {

  val s3folder: S3Folder
  val configuration: Configuration

  private lazy val destination: File = new File(".")
  lazy val dbLocation: File = new File(destination, s3folder.key)

  def instructions: AnyInstructions = LazyTry {
    println(s"""Dowloading
      |from: ${s3folder.url}
      |to: ${destination.getCanonicalPath}
      |""".stripMargin)

    val transferManager = new TransferManager(new InstanceProfileCredentialsProvider())
    transferManager.downloadDirectory(s3folder.bucket, s3folder.key, destination).waitForCompletion
  }
}

case object DefaultConfig {

  def apply(location: File): Configuration = {
    val base = new BaseConfiguration()
    base.setProperty("storage.directory", location.getCanonicalPath)
    base.setProperty("storage.backend", "berkeleyje")
    base.setProperty("storage.batch-loading", "false")
    base.setProperty("storage.transactions", "true")
    base.setProperty("query.fast-property", "false")
    base.setProperty("schema.default", "none")
    base
  }
}

abstract class Bio4jDist(
  val region: Region,
  val version: String,
  val name: String
) extends AnyBio4jDist { dist =>

  lazy val s3folder: S3Folder = S3Folder(
    s"${dist.region}.releases.bio4j.com",
    s"${dist.version}/${dist.name}"
  )

  lazy val configuration: Configuration = DefaultConfig(dbLocation)
}


// TODO: should it be somewhere else?
case object bio4jNCBITaxonomy extends AnyBio4jDist {

  lazy val s3folder: S3Folder = S3Folder("resources.ohnosequences.com", "16s/bio4j")

  lazy val configuration: Configuration = DefaultConfig(dbLocation)

  // the graph; its only (direct) use is for indexes
  // FIXME: this works but still with errors, should be fixed (something about transactions)
  lazy val graph: TitanNCBITaxonomyGraph =
    new TitanNCBITaxonomyGraph(
      new DefaultTitanGraph(TitanFactory.open(configuration))
    )
}
