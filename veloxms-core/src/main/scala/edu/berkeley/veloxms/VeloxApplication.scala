package edu.berkeley.veloxms

import edu.berkeley.veloxms.resources._
import edu.berkeley.veloxms.storage._
import net.nicktelford.dropwizard.scala.ScalaApplication
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import tachyon.TachyonURI
import tachyon.r.sorted.ClientStore
import scala.util.{Try, Success, Failure}
import io.dropwizard.Configuration
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.validator.constraints.NotEmpty
import com.typesafe.scalalogging.LazyLogging


case class VeloxConfiguration(
    @NotEmpty itemModelLoc: String,
    @NotEmpty userModelLoc: String,
    @NotEmpty ratingsLock: String,
    @NotNull numFactors: Integer,
    ) extends Configuration

object VeloxApplication extends ScalaApplication[VeloxConfiguration] with LazyLogging {

    override def getName = "velox model server"

    // val LOGGER = Logger(LoggerFactory.getLogger(VeloxApplication.class))

    override def init(bootstrap: Bootstrap[VeloxConfiguration]) {
        // bootstrap.addBundle(new ScalaBundle)
        // init global state
    }

    override def run(config: VeloxConfiguration, env: Environment) {
        
        val userModel = TachyonUtils.getStore(config.getUserModelLoc) match {
            case Success(s) => s
            case Failure(f) => throw new RuntimeException(
                s"Couldn't initialize use model: ${f.getMessage}")
        }

        val itemModel = TachyonUtils.getStore(config.getItemModelLoc) match {
            case Success(s) => s
            case Failure(f) => throw new RuntimeException(
                s"Couldn't initialize item model: ${f.getMessage}")
        }

        val ratings = TachyonUtils.getStore(config.getRatingsLoc) match {
            case Success(s) => s
            case Failure(f) => throw new RuntimeException(
                s"Couldn't initialize use model: ${f.getMessage}")
        }

        val model = new TachyonStorage(userModel, itemModel, ratings, config.getNumFactors)
        env.jersey().register(new PredictItemResource(model))
        env.jersey().register(addRatings)

}







