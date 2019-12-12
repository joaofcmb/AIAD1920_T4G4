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
from sklearn.metrics import classification_report, confusion_matrix, accuracy_score, roc_curve, auc
from sklearn.metrics import plot_precision_recall_curve, plot_confusion_matrix, average_precision_score


class Metrics:
    """
    Metrics class where all classification model metrics
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
        self.classification_report()
        self.accuracy_score()
        self.feature_importance()
        self.confusion_matrix()
        self.learning_curve()
        self.roc_curve()
        self.precision_recall_curve()

    def classification_report(self):
        """
        Function responsible for displaying the model
        classification report including precision, recall,
        f1-score and support
        @return: classification report
        """
        print('Classification report: \n')
        print(classification_report(self.model.dataset.get_y_test(), self.model.predicted))

    def accuracy_score(self):
        """
        Function responsible for displaying the accuracy score
        @return: accuracy score
        """
        print('Accuracy score: ' + str(accuracy_score(self.model.dataset.get_y_test(),
                                                      self.model.get_predicted())) + '\n')

    def confusion_matrix(self):
        """
        Function responsible for displaying the confusion
        matrix
        @return: confusion matrix
        """
        np.set_printoptions(precision=2)

        # Plot non-normalized confusion matrix
        titles_options = [(self.model.algorithm + " - Confusion matrix, without normalization", None),
                          (self.model.algorithm + " - Normalized confusion matrix", 'true')]
        for title, normalize in titles_options:
            disp = plot_confusion_matrix(self.model.clf, self.model.dataset.get_x_test(),
                                         self.model.dataset.get_y_test(), cmap=plt.cm.Blues, normalize=normalize)
            disp.ax_.set_title(title)

            print(title)
            print(disp.confusion_matrix)
            print()

        plt.show()

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

    def roc_curve(self):
        """
        Function responsible for displaying the ROC curve
        @return: ROC curve
        """
        metrics.plot_roc_curve(self.model.clf, self.model.dataset.get_x_test(), self.model.dataset.get_y_test())
        plt.title(self.model.algorithm + ' - ROC curve')
        plt.show()

    def precision_recall_curve(self):
        """
        Function responsible for displaying the
        precision-recall curve
        @return: precision-recall curve
        """
        disp = plot_precision_recall_curve(self.model.clf, self.model.dataset.get_x_test(),
                                           self.model.dataset.get_y_test())
        disp.ax_.set_title(self.model.algorithm + ' - Precision-Recall curve')
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
            graph.render("exports/DecisionTreeClassifier")
