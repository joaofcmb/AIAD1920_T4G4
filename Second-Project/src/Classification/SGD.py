"""
 Import Module
"""

import os
from joblib import load, dump
from sklearn.linear_model import SGDClassifier
from src.Classification.Model import Model
from sklearn.model_selection import GridSearchCV


class SGD(Model):
    """
    SGD model class
    """
    algorithm = 'SGD'

    # Default Tuning parameters
    default_parameters = {'loss': ['hinge', 'log', 'modified_huber', 'squared_hinge', 'perceptron'],
                          'penalty': ['l2', 'l1', 'elasticnet'],
                          'alpha': [0.0001],
                          'l1_ratio': [0.15],
                          'fit_intercept': [True, False],
                          'max_iter': [1000],
                          'tol': [0.001],
                          'shuffle': [True, False],
                          'verbose': [0],
                          'epsilon': [0.1],
                          'n_jobs': [None],
                          'random_state': [None],
                          'learning_rate': ['optimal'],
                          'eta0': [0.0],
                          'power_t': [0.5],
                          'early_stopping': [True, False],
                          'validation_fraction': [0.1],
                          'n_iter_no_change': [5],
                          'class_weight': [None],
                          'warm_start': [True, False],
                          'average': [True, False]
                          }

    # Tuning parameters
    tuning_parameters = {}

    def __init__(self, grid_search=False, filename='round.csv'):
        """
        SGD class constructor

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
                clf = GridSearchCV(SGDClassifier(), self.tuning_parameters)
                dump(clf, 'joblib/GridSearchCV_' + self.algorithm + '.joblib')
        else:
            if os.path.isfile('joblib/' + self.algorithm + '.joblib'):
                clf = load('joblib/' + self.algorithm + '.joblib')
            else:
                clf = SGDClassifier()

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

    def get_y_score(self):
        """
        Function responsible for retrieving the y_score
        which consists on the array having all
        estimations
        @return: y_score
        """
        return self.clf.decision_function(self.dataset.get_y_test())

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
