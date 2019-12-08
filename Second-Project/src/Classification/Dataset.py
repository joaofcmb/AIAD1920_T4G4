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
                train_data = []
                for card in row[:5]:
                    train_data.append(Dataset.card_to_feature(card))

                players_info = row[5:-1]
                num_players = int(len(players_info) / 2)

                for elem in players_info[:num_players]:
                    train_data.append(float(elem))

                train_data.extend([0.0] * (8 - num_players))

                for elem in players_info[num_players:]:
                    train_data.append(float(elem))

                train_data.extend([0.0] * (8 - num_players))

                self.x.append(train_data)
                self.y.append(row[-1])

    @staticmethod
    def card_to_feature(card):
        """
        Converts a card in the format RANK-SUIT into
        a numeric value
        @param card: card to be converted
        @return:
        """
        suit = {'Spades': 0, 'Clubs': 13, 'Hearts': 26, 'Diamonds': 39}
        rank = {'2': 0, '3': 1, '4': 2, '5': 3, '6': 4, '7': 5, '8': 6, '9': 7, '10': 8,
                'Jack': 9, 'Queen': 10, 'King': 11, 'Ace': 12}

        card_info = card.split('-')
        return rank[card_info[0]] + suit[card_info[1]]

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
