package org.slowaner.logger

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.util.StatusPrinter
import org.slf4j.LoggerFactory

import java.nio.file.Paths

/**
  * Companion object that initializes and configures [[ch.qos.logback.classic.LoggerContext]] for logger
  *
  * @note To initialize [[ch.qos.logback.classic.LoggerContext]] just call function [[org.slowaner.logger.Logger#init()]].
  *       This function does nothing but making [[ClassLoader]] load [[org.slowaner.logger.Logger]]
  * @author Slowaner
  * @since 0.0.1
  * @version 1.0.0
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
  private val loggerContext = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
  private val logger = loggerContext.getLogger(this.getClass)
  logger.debug("LoggerContext started being initializing")
  logger.debug("LoggerContext is initialized from default config")

  //  private val DefaultLogbackConfigurationResourceName = "logback.xml"
  //  private val loggerContext = new LoggerContext

  logger.debug("Try to load custom configuration")
  private val loggerCustomConfigFile = Paths.get("conf/logback.xml").toAbsolutePath.normalize().toFile
  if (loggerCustomConfigFile.isFile && loggerCustomConfigFile.canRead)
    try {
      logger.debug(s"Custom configuration found at ${loggerCustomConfigFile.toPath.toString}. Start loading")
      val configurator = new JoranConfigurator()
      configurator.setContext(loggerContext)
      // Call context.reset() to clear any previous configuration, e.g. default
      // configuration. For multi-step configuration, omit calling context.reset().
      loggerContext.reset()
      configurator.doConfigure(loggerCustomConfigFile)
      logger.debug(s"Custom configuration at ${loggerCustomConfigFile.toPath.toString} is loaded")
    } catch {
      case _: JoranException =>
        logger.error(s"Can't load custom configuration at ${loggerCustomConfigFile.toPath.toString}")
      // StatusPrinter will handle this
    }
  else logger.debug(s"Custom configuration not found")

  logger.debug(s"Channel LoggerContext errors and warnings to application logger")
  StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext)

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
    * Does nothing. Just makes [[ClassLoader]] load this class
    *
    * @since 1.0
    */
  def init(): Unit = {}
}
