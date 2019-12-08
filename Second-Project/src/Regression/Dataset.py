"""
Import module
"""
import csv
from sklearn.model_selection import train_test_split


class Dataset:
    """
    Regression dataset class where all information about the
    the regression dataset will be stored and manipulated
    """

    def __init__(self, filename, target='hand-selection', ds_test_perc=0.25):
        """
        Regression dataset class default constructor
        @param filename: file to be read
        @param target: defines whether regression is aiming hand-selection (default) or aggression
        @param ds_test_perc: dataset test percentage used
        """
        self.filename = 'dataset/' + filename
        self.ds_test_perc = ds_test_perc
        self.target = target
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
                bet_actions = {'All in': 0, 'Bet': 0, 'Call': 0, 'Check': 0, 'Fold': 0, 'Raise': 0}
                bet_values = {'Call': 0, 'Bet': 0, 'Raise': 0}

                for action in row[:-2]:
                    if 'Call' in action or 'Bet' in action or 'Raise' in action:
                        action_info = action.split('-')
                        bet_values[action_info[0]] += int(action_info[1])
                        bet_actions[action_info[0]] += 1
                    else:
                        bet_actions[action] += 1

                self.x.append([bet_actions['All in'], bet_actions['Bet'], bet_actions['Call'], bet_actions['Check'],
                               bet_actions['Fold'], bet_actions['Raise'], bet_values['Call'], bet_values['Bet'],
                               bet_values['Raise']])

                if self.target == 'hand-selection':
                    self.y.append(float(row[-2]))
                else:
                    self.y.append(float(row[-1]))

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
