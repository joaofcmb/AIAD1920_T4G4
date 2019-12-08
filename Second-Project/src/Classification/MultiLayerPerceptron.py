"""
 Import Module
"""

import os
from joblib import load, dump
from sklearn.neural_network import MLPClassifier
from src.Classification.Model import Model
from sklearn.model_selection import GridSearchCV


class MultiLayerPerceptron(Model):
    """
    MultiLayerPerceptron model class
    """
    algorithm = 'MultiLayerPerceptron'

    # Default Tuning parameters
    default_parameters = {'hidden_layer_sizes': [100],
                          'activation': ['identity', 'logistic', 'tanh', 'relu'],
                          'solver': ['lbfgs', 'sgd', 'adam'],
                          'alpha': [0.0001],
                          'batch_size': ['auto'],
                          'learning_rate': ['constant', 'invscaling', 'adaptive'],
                          'learning_rate_init': [0.001],
                          'power_t': [0.5],
                          'max_iter': [200],
                          'shuffle': [True, False],
                          'random_state': [None],
                          'tol': [1e-4],
                          'verbose': [True, False],
                          'warm_start': [True, False],
                          'momentum': [0.9],
                          'nesterovs_momentum': [True, False],
                          'early_stoppingbool': [True, False],
                          'validation_fraction': [0.1],
                          'beta_1': [0.9],
                          'beta_2': [0.999],
                          'epsilon': [1e-8],
                          'n_iter_no_change': [10],
                          'max_fun': [15000]
                          }

    # Tuning parameters
    tuning_parameters = {}

    def __init__(self, grid_search=False, filename='round.csv'):
        """
        MultiLayerPerceptron class constructor

        @param grid_search: indicates whether classifier should be created
                            with the grid search classifier
        """
        super().__init__(self.get_classifier(grid_search), self.algorithm, filename)

    def get_classifier(self, grid_search):
        """
        Function responsible for getting the model
        classifier. If already created it loads it from
        the respective file otherwise creates it.
        """
        self.grid_search = grid_search

        if grid_search:
            if os.path.isfile('joblib/GridSearchCV_' + self.algorithm + '.joblib'):
                clf = load('joblib/GridSearchCV_' + self.algorithm + '.joblib')
            else:
                clf = GridSearchCV(MLPClassifier(), self.tuning_parameters)
                dump(clf, 'joblib/GridSearchCV_' + self.algorithm + '.joblib')
        else:
            if os.path.isfile('joblib/' + self.algorithm + '.joblib'):
                clf = load('joblib/' + self.algorithm + '.joblib')
            else:
                clf = MLPClassifier()

        return clf

    def get_algorithm(self):
        """
        Function responsible for retrieving the
        algorithm name
        @return: algorithm name
        """
        return self.algorithm

    def get_algorithm_gs_param(self):
        """
        Function responsible for retrieving the grid
        search parameters
        @return: grid search parameters
        """
        return self.tuning_parameters

    def get_best_param(self):
        """
        Function responsible for showing the best
        parameters for this specific algorithm
        @return:
        """
        if self.grid_search:
            value = "Best parameters for " + self.algorithm + " algorithm:\n"
            for param_name in self.tuning_parameters:
                value = value + param_name + ": " + str(self.clf.best_params_[param_name]) + '\n'

            return value
