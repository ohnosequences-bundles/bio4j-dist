package ohnosequencesBundles.statika

import ohnosequences.statika._
import ohnosequences.awstools._, s3._, regions._
import org.apache.commons.configuration.Configuration
import org.apache.commons.configuration.BaseConfiguration
import java.io.File


trait AnyBio4jDist extends Bundle() {

  val s3folder: S3Folder
  val configuration: Configuration

  private lazy val destination: File = new File(".")
  lazy val dbLocation: File = new File(destination, s3folder.key)

  def instructions: AnyInstructions = LazyTry {
    println(s"""Dowloading
      |from: ${s3folder}
      |to: ${destination.getCanonicalPath}
      |""".stripMargin)

    s3.defaultClient.withTransferManager { tm =>
      tm.download(s3folder, destination)
    }
  }
}

case object DefaultBio4jTitanConfig {

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
    dist.version
  ) / dist.name /

  lazy val configuration: Configuration = DefaultBio4jTitanConfig(dbLocation)
}


abstract class Bio4jLiteDist(
  region: Region,
  version: String
) extends Bio4jDist(region, version, "bio4j_all_but_uniref_and_gi_index") {

  // TODO: many graphs here?
  // lazy val graph: TitanNCBITaxonomyGraph =
  //   new TitanNCBITaxonomyGraph(
  //     new DefaultTitanGraph(TitanFactory.open(configuration))
  //   )
}


abstract class Bio4jFullDist(
  region: Region,
  version: String
) extends Bio4jDist(region, version, "bio4j_all_plus_isoforms")
