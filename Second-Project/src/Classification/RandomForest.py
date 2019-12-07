"""
 Import Module
"""

import os
from joblib import load, dump
from sklearn.ensemble import RandomForestClassifier
from src.Classification.Model import Model
from sklearn.model_selection import GridSearchCV


class RandomForest(Model):
    """
    RandomForest model class
    """
    algorithm = 'RandomForest'

    # Default Tuning parameters
    default_parameters = {'n_estimators': [100],
                          'criterion': ['gini', 'entropy'],
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

    def __init__(self, grid_search=False, filename='round.csv'):
        """
        RandomForest class constructor

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
            if os.path.isfile('../joblib/GridSearchCV_' + self.algorithm + '.joblib'):
                clf = load('../joblib/GridSearchCV_' + self.algorithm + '.joblib')
            else:
                clf = GridSearchCV(RandomForestClassifier(), self.tuning_parameters)
                dump(clf, '../joblib/GridSearchCV_' + self.algorithm + '.joblib')
        else:
            if os.path.isfile('../joblib/' + self.algorithm + '.joblib'):
                clf = load('../joblib/' + self.algorithm + '.joblib')
            else:
                clf = RandomForestClassifier()

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
