import tflearn
from tflearn.layers.core import input_data, dropout, fully_connected
from tflearn.layers.estimator import regression
from statistics import median, mean
from collections import Counter
import numpy as np
import tensorflow as tf
from tensorflow.python.framework import graph_util

def create_dummy_model(training_data):
    shape_second_parameter = len(training_data[0][0])
    x = np.array([i[0] for i in training_data])
    X = x.reshape(-1, shape_second_parameter, 1)
    y = [i[1] for i in training_data]
    model = create_neural_network_model(input_size=len(X[0]), output_size=len(y[0]))
    return model
 
def create_neural_network_model(input_size, output_size):
    network = input_data(shape=[None, input_size, 1], name='input')
    network = tflearn.fully_connected(network, 32)
    network = tflearn.fully_connected(network, 32)
    network = fully_connected(network, output_size, activation='softmax')
    network = regression(network, name='targets')
    model = tflearn.DNN(network, tensorboard_dir='tflearn_logs')
 
    return model

def freeze_graph(model_folder, output_graph="frozen_model.pb"):
    # We retrieve our checkpoint fullpath
    try:
        checkpoint = tf.train.get_checkpoint_state(model_folder)
        input_checkpoint = checkpoint.model_checkpoint_path
        print("[INFO] input_checkpoint:", input_checkpoint)
    except:
        input_checkpoint = model_folder
        print("[INFO] Model folder", model_folder)

    # Before exporting our graph, we need to precise what is our output node
    # This is how TF decides what part of the Graph he has to keep and what part it can dump
    output_node_names = "FullyConnected_2/Softmax"

    # We clear devices to allow TensorFlow to control on which device it will load operations
    clear_devices = True
    
    # We import the meta graph and retrieve a Saver
    saver = tf.train.import_meta_graph(input_checkpoint + '.meta', clear_devices=clear_devices)

    # We retrieve the protobuf graph definition
    graph = tf.get_default_graph()
    input_graph_def = graph.as_graph_def()

    # We start a session and restore the graph weights
    with tf.Session() as sess:
        saver.restore(sess, input_checkpoint)

        # We use a built-in TF helper to export variables to constants
        output_graph_def = graph_util.convert_variables_to_constants(
            sess,                        # The session is used to retrieve the weights
            input_graph_def,             # The graph_def is used to retrieve the nodes 
            output_node_names.split(",")  # The output node names are used to select the usefull nodes
        ) 

        # Finally we serialize and dump the output graph to the filesystem
        with tf.gfile.GFile(output_graph, "wb") as f:
            f.write(output_graph_def.SerializeToString())
        print("%d ops in the final graph." % len(output_graph_def.node))

        print("[INFO] output_graph:",output_graph)
        print("[INFO] all done")