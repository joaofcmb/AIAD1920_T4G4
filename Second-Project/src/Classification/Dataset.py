"""
Import module
"""
import csv
from sklearn.model_selection import train_test_split


class Dataset:
    """
    Classification dataset class where all information about the
    the classification dataset will be stored and manipulated
    """

    def __init__(self, filename, ds_test_perc=0.25):
        """
        Classification dataset class default constructor
        @param filename: file to be read
        @param ds_test_perc: dataset test percentage used
        """
        self.filename = 'dataset/' + filename
        self.feature_names = ['effective_hand_strength', 'hand_selection_1', 'hand_selection_2', 'hand_selection_3',
                              'hand_selection_4', 'hand_selection_5', 'hand_selection_6', 'hand_selection_7',
                              'hand_selection_8', 'aggression_1', 'aggression_2', 'aggression_3', 'aggression_4',
                              'aggression_5', 'aggression_6', 'aggression_7', 'aggression_8']
        self.ds_test_perc = ds_test_perc
        self.x, self.y = [], []

        self.parse_dataset()
        self.x_train, self.x_test, self.y_train, self.y_test = train_test_split(self.x, self.y,
                                                                                test_size=self.ds_test_perc,
                                                                                random_state=0)

    def parse_dataset(self):
        """
        Parses the dataset and fills up the content of the
        x and y attributes
        @return:
        """
        with open(self.filename) as csv_file:
            csv_reader = csv.reader(csv_file, delimiter=',')

            for row in csv_reader:
                train_data = [float(row[0])]

                players_info = row[1:-1]
                num_players = int(len(players_info) / 2)

                for elem in players_info[:num_players]:
                    train_data.append(float(elem))

                train_data.extend([0.0] * (8 - num_players))

                for elem in players_info[num_players:]:
                    train_data.append(float(elem))

                train_data.extend([0.0] * (8 - num_players))

                self.x.append(train_data)
                self.y.append(row[-1])

    def get_x_train(self):
        """
        Retrieves [n_samples, n_features] training samples
        @return: x_train
        """
        return self.x_train

    def get_x_test(self):
        """
        Retrieves [n_samples, n_features] testing samples
        @return: x_test
        """
        return self.x_test

    def get_y_train(self):
        """
        Retrieves [n_samples] training target values
        @return: x_train
        """
        return self.y_train

    def get_y_test(self):
        """
        Retrieves [n_samples] testing target values
        @return: x_train
        """
        return self.y_test
