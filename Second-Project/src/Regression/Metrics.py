"""
 Import Module
"""
import graphviz
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn import tree
from sklearn import metrics
from inspect import signature
from sklearn.model_selection import learning_curve
from sklearn.metrics import mean_gamma_deviance
from sklearn.metrics import explained_variance_score, max_error, mean_absolute_error, mean_squared_error
from sklearn.metrics import mean_squared_log_error, median_absolute_error, r2_score, mean_poisson_deviance


class Metrics:
    """
    Metrics class where all regression model metrics
    are defined
    """

    def __init__(self, model):
        """
        Metrics class constructor
        @param model: model to be analysed
        """
        self.model = model

    def show_all(self):
        """
        Function responsible for displaying all the model
        associated metrics
        """
        self.explained_variance_score()
        self.max_error()
        self.mean_absolute_error()
        self.mean_squared_error()
        self.median_absolute_error()
        self.r2_score()
        self.mean_poisson_deviance()
        self.mean_gamma_deviance()
        self.feature_importance()
        self.learning_curve()

    def explained_variance_score(self):
        """
        Displays the explained variance score
        """
        print('Explained variance score: ' + str(explained_variance_score(self.model.dataset.get_y_test(),
                                                                          self.model.get_predicted())))

    def max_error(self):
        """
        Displays the maximum residual error
        """
        print('Maximum residual error: ' + str(max_error(self.model.dataset.get_y_test(), self.model.get_predicted())))

    def mean_absolute_error(self):
        """
        Displays the mean absolute error regression loss
        """
        print('Mean absolute error regression loss: ' + str(mean_absolute_error(self.model.dataset.get_y_test(),
                                                                                self.model.get_predicted())))

    def mean_squared_error(self):
        """
        Displays the mean squared error regression loss
        """
        print('Mean squared error regression loss: ' + str(mean_squared_error(self.model.dataset.get_y_test(),
                                                                              self.model.get_predicted())))

    def median_absolute_error(self):
        """
        Displays the median absolute error regression loss
        """
        print('Median absolute error regression loss: ' + str(median_absolute_error(self.model.dataset.get_y_test(),
                                                                                    self.model.get_predicted())))

    def r2_score(self):
        """
        Displays the R^2 (coefficient of determination) regression score function
        """
        print('R^2 (coefficient of determination) regression score function: ' +
              str(r2_score(self.model.dataset.get_y_test(), self.model.get_predicted())))

    def mean_poisson_deviance(self):
        """
        Displays the mean Poisson deviance regression loss
        """
        print('Mean Poisson deviance regression loss: ' + str(mean_poisson_deviance(self.model.dataset.get_y_test(),
                                                                                    self.model.get_predicted())))

    def mean_gamma_deviance(self):
        """
        Displays the mean Gamma deviance regression loss
        """
        print('Mean Gamma deviance regression loss: ' + str(mean_gamma_deviance(self.model.dataset.get_y_test(),
                                                                                self.model.get_predicted())))

    def learning_curve(self, axes=None, ylim=None, cv=None):
        """
        Generate 3 plots: the test and training learning curve, the training
        samples vs fit times curve, the fit times vs score curve.
        @return: learning curve
        """
        if axes is None:
            _, axes = plt.subplots(1, 3, figsize=(20, 5))

        axes[0].set_title(self.model.algorithm + " - Learning curve")
        if ylim is not None:
            axes[0].set_ylim(*ylim)
        axes[0].set_xlabel("Training examples")
        axes[0].set_ylabel("Score")

        train_sizes, train_scores, test_scores, fit_times, _ = \
            learning_curve(self.model.clf, self.model.dataset.x, self.model.dataset.y, cv=cv, n_jobs=None,
                           train_sizes=np.linspace(.1, 1.0, 5),
                           return_times=True)
        train_scores_mean = np.mean(train_scores, axis=1)
        train_scores_std = np.std(train_scores, axis=1)
        test_scores_mean = np.mean(test_scores, axis=1)
        test_scores_std = np.std(test_scores, axis=1)
        fit_times_mean = np.mean(fit_times, axis=1)
        fit_times_std = np.std(fit_times, axis=1)

        # Plot learning curve
        axes[0].grid()
        axes[0].fill_between(train_sizes, train_scores_mean - train_scores_std,
                             train_scores_mean + train_scores_std, alpha=0.1,
                             color="r")
        axes[0].fill_between(train_sizes, test_scores_mean - test_scores_std,
                             test_scores_mean + test_scores_std, alpha=0.1,
                             color="g")
        axes[0].plot(train_sizes, train_scores_mean, 'o-', color="r",
                     label="Training score")
        axes[0].plot(train_sizes, test_scores_mean, 'o-', color="g",
                     label="Cross-validation score")
        axes[0].legend(loc="best")

        # Plot n_samples vs fit_times
        axes[1].grid()
        axes[1].plot(train_sizes, fit_times_mean, 'o-')
        axes[1].fill_between(train_sizes, fit_times_mean - fit_times_std,
                             fit_times_mean + fit_times_std, alpha=0.1)
        axes[1].set_xlabel("Training examples")
        axes[1].set_ylabel("fit_times")
        axes[1].set_title(self.model.algorithm + " - Scalability of the model")

        # Plot fit_time vs score
        axes[2].grid()
        axes[2].plot(fit_times_mean, test_scores_mean, 'o-')
        axes[2].fill_between(fit_times_mean, test_scores_mean - test_scores_std,
                             test_scores_mean + test_scores_std, alpha=0.1)
        axes[2].set_xlabel("fit_times")
        axes[2].set_ylabel("Score")
        axes[2].set_title(self.model.algorithm + " - Performance of the model")

        plt.show()

    def feature_importance(self):
        """
        Function responsible for displaying the
        feature importance
        @return: feature importance
        """
        if self.model.algorithm == 'DecisionTree' or self.model.algorithm == 'RandomForest':
            print("Feature importance\n")
            print(pd.DataFrame(self.model.clf.feature_importances_, index=self.model.dataset.feature_names,
                               columns=['Importance']).sort_values('Importance', ascending=False))

    def export(self):
        """
        Exports the decision tree graph
        """
        if self.model.algorithm == 'DecisionTree':
            dot_data = tree.export_graphviz(self.model.clf, out_file=None)
            graph = graphviz.Source(dot_data)
            graph.render("exports/DecisionTreeRegressor")
