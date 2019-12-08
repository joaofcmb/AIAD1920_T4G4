"""
 Import Module
"""

import os
from joblib import load, dump
from sklearn import tree
from src.Classification.Model import Model
from sklearn.model_selection import GridSearchCV


class DecisionTree(Model):
    """
    DecisionTree model class
    """
    algorithm = 'DecisionTree'

    # Default Tuning parameters
    default_parameters = {'criterion': ['gini', 'entropy'],
                          'splitter': ['best', 'random'],
                          'max_depth': [None],
                          'min_samples_split': [2],
                          'min_samples_leaf': [1],
                          'min_weight_fraction_leaf': [0.],
                          'max_features': [None, 'auto', 'sqrt', 'log2'],
                          'random_state': [None],
                          'max_leaf_nodes': [None],
                          'min_impurity_decrease': [0.],
                          'class_weight': ['balanced'],
                          'ccp_alpha': [0.0]
                          }

    # Tuning parameters
    tuning_parameters = {}

    def __init__(self, grid_search=False, filename='round.csv'):
        """
        DecisionTree class constructor

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
                clf = GridSearchCV(tree.DecisionTreeClassifier(), self.tuning_parameters)
                dump(clf, 'joblib/GridSearchCV_' + self.algorithm + '.joblib')
        else:
            if os.path.isfile('joblib/' + self.algorithm + '.joblib'):
                clf = load('joblib/' + self.algorithm + '.joblib')
            else:
                clf = tree.DecisionTreeClassifier()

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
