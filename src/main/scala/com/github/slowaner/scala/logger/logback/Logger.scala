package com.github.slowaner.scala.logger.logback

import java.io.File
import java.net.{URI, URL}

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.{Logger => LogbackLogger}
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.util.StatusPrinter
import org.slf4j.LoggerFactory
import java.nio.file.Paths

/**
  * Companion object that initializes and configures [[ch.qos.logback.classic.LoggerContext]] for logger
  *
  * @note To initialize [[ch.qos.logback.classic.LoggerContext]] just call function [[Logger#init()]].
  *       This function does nothing but making [[ClassLoader]] load [[Logger]]
  * @author Slowaner
  * @since 0.0.1
  * @version 1.1.0
  *
  */
object Logger {

  /* private var logFile = Paths.get(Configuration.getString("logger.logFileName"))
   if (!logFile.isAbsolute) {
     logFile = Paths.get("").resolve(logFile)
   }
   logFile = logFile.toAbsolutePath.normalize

   private val logLevel = Configuration.getString("logger.logLevel")
   private val logFileNamePattern = Configuration.getString("logger.logFileNamePattern")
   private val logMaxFileSize = Configuration.getString("logger.logMaxFileSize")
   private val logMaxHistory = Configuration.getInt("logger.logMaxHistory")
   private val logTotalSizeCap = Configuration.getString("logger.logTotalSizeCap")*/

  /**
    * Loaded [[ch.qos.logback.classic.LoggerContext]] with default configuration.
    * ===Configuration loads from ClassPath in order:===
    * <ol>
    * <li>logback-test.xml</li>
    * <li>logback.groove</li>
    * <li>logback.xml</li>
    * </ol>
    */

  private var loggerContext: LoggerContext = _
  private var logger: LogbackLogger = _

  def isInitialized: Boolean = loggerContext != null

  private def checkInitialized(): Unit = if (isInitialized) sys.error("Slf4j logback not initialized")

  private def checkNotInitialized(): Unit = if (isInitialized) sys.error("Slf4j logback is already initialized")

  /*private val fileAppender = loggerContext.getLogger(slf4j.Logger.ROOT_LOGGER_NAME).getAppender("FILE").asInstanceOf[RollingFileAppender[ILoggingEvent]]

  if (fileAppender != null) {
    if (Paths.get(fileAppender.getFile).toAbsolutePath.normalize != logFile) {
      fileAppender.stop()
      fileAppender.setFile(logFile.toString)
//      val pl = new PatternLayout
//      pl.setPattern("%d %5p %t [%c:%L] %m%n)")
//      pl.setContext(lc)
//      pl.start()
//      fileAppender.setLayout(pl)
//      fileAppender.setContext(lc)
    }

    val appenderRollingPolicy = fileAppender.getRollingPolicy.asInstanceOf[SizeAndTimeBasedRollingPolicy[ILoggingEvent]]
    if (appenderRollingPolicy != null) {
      appenderRollingPolicy.stop()

      if (Paths.get(appenderRollingPolicy.getFileNamePattern).toAbsolutePath.normalize != Paths.get(logFileNamePattern).toAbsolutePath.normalize) {
        appenderRollingPolicy.setFileNamePattern(logFileNamePattern)
      }

      if (appenderRollingPolicy.getMaxHistory != logMaxHistory) {
        appenderRollingPolicy.setMaxHistory(logMaxHistory)
      }

      // Set MaxFileSize and TotalSizeCap anyway
      appenderRollingPolicy.setMaxFileSize(FileSize.valueOf(logMaxFileSize))
      appenderRollingPolicy.setTotalSizeCap(FileSize.valueOf(logTotalSizeCap))

      if (!appenderRollingPolicy.isStarted)
        appenderRollingPolicy.start()
    }

    if (!fileAppender.isStarted) {
      fileAppender.start()
    }
  }*/

  /**
    * <p>Initializes LoggerContext with default configurations.</p>
    * <p>Set system property "`logback.configurationFile`"
    * to override default configuration file</p>
    *
    * @note System property "`logback.configurationFile`" prefered to be URL,
    *       but also can be Resource reference or File path
    * @throws RuntimeException if already initialized
    * @since 1.0
    */
  def init(): Unit = {
    checkNotInitialized()
    loggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    logger = loggerContext.getLogger(this.getClass)

    logger.debug("LoggerContext started being initializing")
    logger.debug("LoggerContext is initialized from default config")


    logger.debug(s"Channel LoggerContext errors and warnings to application logger")
    StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext)
  }

  /**
    * <p>Initializes LoggerContext with configuration loaded from `source`.</p>
    * <p>It simply sets "`logback.configurationFile`" system property
    * and calls `init()` without parameters</p>
    *
    * @param source source to load configuration from
    * @see [[com.github.slowaner.scala.logger.logback.Logger#init()]]
    * @throws RuntimeException if already initialized
    * @since 1.1
    */
  def init(source: String): Unit = {
    checkNotInitialized()
    sys.props.put("logback.configurationFile", source)
    this.init()
  }

  /**
    * <p>Initializes LoggerContext with configuration loaded from `url`.</p>
    * <p>It simply calls `init(url.toString)`</p>
    *
    * @param url url to load configuration from
    * @see [[com.github.slowaner.scala.logger.logback.Logger#init(String)]]
    * @throws RuntimeException if already initialized
    * @since 1.1
    */
  def init(url: URL): Unit = {
    this.init(url.toString)
  }

  /**
    * <p>Initializes LoggerContext with configuration loaded from `uri`.</p>
    * <p>It simply calls `init(uri.toUrl.toString)`</p>
    *
    * @param uri uri to load configuration from
    * @see [[com.github.slowaner.scala.logger.logback.Logger#init(String)]]
    * @throws RuntimeException if already initialized
    * @since 1.1
    */
  def init(uri: URI): Unit = {
    this.init(uri.toURL.toString)
  }

  /**
    * Load configuration from `configurationFile`
    *
    * @param configurationFile file to load configuration from
    */
  def loadFrom(configurationFile: File): Unit = {
    checkInitialized()
    logger.debug("Try to load custom configuration from {}", configurationFile)
    if (configurationFile.isFile && configurationFile.canRead)
      try {
        logger.debug("Custom configuration found at {}. Start loading", configurationFile)
        val configurator = new JoranConfigurator()
        configurator.setContext(loggerContext)
        // Call context.reset() to clear any previous configuration, e.g. default
        // configuration. For multi-step configuration, omit calling context.reset().
        loggerContext.reset()
        configurator.doConfigure(configurationFile)
        logger.debug("Custom configuration at {} is loaded", configurationFile)
      } catch {
        case ex: JoranException =>
          logger.error(s"Can't load custom configuration at $configurationFile", ex)
        // StatusPrinter will handle this
      }
    else logger.debug("Custom configuration not found")
  }

  /**
    * Load configuration from `configurationURL`
    *
    * @param configurationURL URL to load configuration from
    */
  def loadFrom(configurationURL: URL): Unit = {
    checkInitialized()
    logger.debug("Try to load custom configuration from {}", configurationURL)
    try {
      val configurator = new JoranConfigurator()
      configurator.setContext(loggerContext)
      // Call context.reset() to clear any previous configuration, e.g. default
      // configuration. For multi-step configuration, omit calling context.reset().
      loggerContext.reset()
      configurator.doConfigure(configurationURL)
      logger.debug("Custom configuration at {} is loaded", configurationURL)
    } catch {
      case ex: JoranException =>
        logger.error(s"Can't load custom configuration at $configurationURL", ex)
      // StatusPrinter will handle this
    }
  }
}
