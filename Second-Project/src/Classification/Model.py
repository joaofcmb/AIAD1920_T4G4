"""
 Import Module
"""
from joblib import dump
from src.Classification.Dataset import Dataset


class Model:
    """
    Model class where all model information is
    present and where all algorithms are defined
    """

    def __init__(self, classifier, algorithm, filename):

        self.clf = classifier
        self.algorithm = algorithm

        self.dataset = Dataset(filename)
        # self.statistics = Statistics(self)

    def train(self):
        """
        Function responsible for training the model
        classifier using the x_train and y_target
        dataset variables
        """
        self.clf = self.clf.fit(self.dataset.get_x_train(), self.dataset.get_y_train())
        dump(self.clf, '../joblib/' + self.algorithm + '.joblib')

    def predict(self):
        """
        Function responsible for predicting data
        from a certain x_test sample
        @return:
        """
        self.predicted = self.clf.predict(self.dataset.get_x_test())
