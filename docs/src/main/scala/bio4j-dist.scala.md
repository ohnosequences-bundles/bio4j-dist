
```scala
package ohnosequencesBundles.statika

import ohnosequences.statika._
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
    s"${dist.version}/${dist.name}"
  )

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

// TODO: prepare full dist

```




[main/scala/bio4j-dist.scala]: bio4j-dist.scala.md