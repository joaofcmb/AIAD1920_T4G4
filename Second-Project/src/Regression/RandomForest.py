"""
 Import Module
"""

import os
from joblib import load, dump
from sklearn.ensemble import RandomForestRegressor
from src.Model import Model
from sklearn.model_selection import GridSearchCV


class RandomForest(Model):
    """
    RandomForest model class
    """
    algorithm = 'RandomForest'

    # Default Tuning parameters
    default_parameters = {'n_estimators': [100],
                          'criterion': ['mse', 'mae'],
                          'max_depth': [None],
                          'min_samples_split': [2],
                          'min_samples_leaf': [1],
                          'min_weight_fraction_leaf': [0.],
                          'max_features': [None, 'auto', 'sqrt', 'log2'],
                          'max_leaf_nodes': [None],
                          'min_impurity_decrease': [0.],
                          'bootstrap': [True, False],
                          'oob_score': [True, False],
                          'n_jobs': [None],
                          'random_state': [None],
                          'verbose': [0],
                          'warm_start': [True, False],
                          'class_weight': ['balanced'],
                          'ccp_alpha': [0.0],
                          'max_samples': [None]
                          }

    # Tuning parameters
    tuning_parameters = {}

    def __init__(self, grid_search=False, filename='personality.csv'):
        """
        RandomForest class constructor

        @param grid_search: indicates whether classifier should be created
                            with the grid search classifier
        """
        super().__init__(self.get_classifier(grid_search), self.algorithm, filename, False)

    def get_classifier(self, grid_search):
        """
        Function responsible for getting the model
        classifier. If already created it loads it from
        the respective file otherwise creates it.
        """
        self.grid_search = grid_search

        if grid_search:
            if os.path.isfile('joblib/regression/GridSearchCV_' + self.algorithm + '.joblib'):
                clf = load('joblib/regression/GridSearchCV_' + self.algorithm + '.joblib')
            else:
                clf = GridSearchCV(RandomForestRegressor(), self.tuning_parameters)
                dump(clf, 'joblib/regression/GridSearchCV_' + self.algorithm + '.joblib')
        else:
            if os.path.isfile('joblib/regression/' + self.algorithm + '.joblib'):
                clf = load('joblib/regression/' + self.algorithm + '.joblib')
            else:
                clf = RandomForestRegressor()

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
