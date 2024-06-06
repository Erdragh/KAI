package dev.erdragh.kai

import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.distribution.UniformDistribution
import org.deeplearning4j.nn.conf.layers.DenseLayer
import org.deeplearning4j.nn.conf.layers.OutputLayer
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.nd4j.evaluation.classification.Evaluation
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.dataset.DataSet
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Sgd
import org.nd4j.linalg.lossfunctions.LossFunctions
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

private val LOGGER = LoggerFactory.getLogger("KAI")

fun main() {
    LOGGER.info("Starting KAI")

    val modelFile = File("model.zip")
    var net: MultiLayerNetwork

    LOGGER.info("Preparing Data...")

    val input = Nd4j.zeros(4, 2)
    val labels = Nd4j.zeros(4, 2)

    // create first dataset
    // when first input=0 and second input=0
    input.putScalar(intArrayOf(0, 0), 0)
    input.putScalar(intArrayOf(0, 1), 0)

    // then the first output fires for false, and the second is 0 (see class comment)
    labels.putScalar(intArrayOf(0, 0), 1)
    labels.putScalar(intArrayOf(0, 1), 0)


    // when first input=1 and second input=0
    input.putScalar(intArrayOf(1, 0), 1)
    input.putScalar(intArrayOf(1, 1), 0)

    // then xor is true, therefore the second output neuron fires
    labels.putScalar(intArrayOf(1, 0), 0)
    labels.putScalar(intArrayOf(1, 1), 1)


    // same as above
    input.putScalar(intArrayOf(2, 0), 0)
    input.putScalar(intArrayOf(2, 1), 1)
    labels.putScalar(intArrayOf(2, 0), 0)
    labels.putScalar(intArrayOf(2, 1), 1)


    // when both inputs fire, xor is false again - the first output should fire
    input.putScalar(intArrayOf(3, 0), 1)
    input.putScalar(intArrayOf(3, 1), 1)
    labels.putScalar(intArrayOf(3, 0), 1)
    labels.putScalar(intArrayOf(3, 1), 0)

    val ds = DataSet(input, labels)

    try {
        net = MultiLayerNetwork.load(modelFile, true)
    } catch (e: IOException) {
        LOGGER.error("Failed to load model from disk")

        val seed: Long = 1234
        val nEpochs = 10000

        LOGGER.info("Network configuration and training..")

        val conf = NeuralNetConfiguration.Builder()
            .updater(Sgd(0.1))
            .seed(seed)
            .biasInit(0.0)
            .miniBatch(false)
            .list()
            .layer(DenseLayer.Builder()
                .nIn(2)
                .nOut(4)
                .activation(Activation.SIGMOID)
                .weightInit(UniformDistribution(0.0, 1.0))
                .build()
            )
            .layer(OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                .nOut(2)
                .activation(Activation.SOFTMAX)
                .weightInit(UniformDistribution(0.0, 1.0))
                .build()
            )
            .build()

        net = MultiLayerNetwork(conf)
        net.init()

        net.setListeners(ScoreIterationListener(100))

        LOGGER.info(net.summary())

        // actual learning
        for (i in 0..<nEpochs) {
            net.fit(ds)
        }

        net.save(File("model.zip"), true)
    }

    val output = net.output(ds.features)
    LOGGER.info(output.toString())

    val eval = Evaluation()
    eval.eval(ds.labels, output)
    LOGGER.info(eval.stats())
}