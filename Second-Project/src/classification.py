import argparse
from src.Classification.SVC import SVC
from src.Classification.SGD import SGD
from src.Classification.KNeighbors import KNeighbors
from src.Classification.GaussianNaiveBayes import GaussianNaiveBayes
from src.Classification.DecisionTrees import DecisionTree
from src.Classification.RandomForest import RandomForest
from src.Classification.MultiLayerPerceptron import MultiLayerPerceptron

algorithms = {'SVC': SVC,
              'SGD': SGD,
              'KNeighbors': KNeighbors,
              'GaussianNaiveBayes': GaussianNaiveBayes,
              'DecisionTree': DecisionTree,
              'RandomForest': RandomForest,
              'MultiLayerPerceptron': MultiLayerPerceptron
              }

parser = argparse.ArgumentParser(description='Supervised Learning - Classification')
parser.add_argument('algorithm', choices=['SVC', 'SGD', 'DecisionTree', 'GaussianNaiveBayes',
                                          'KNeighbors', 'MultiLayerPerceptron', 'RandomForest'],
                    help='set the supervised learning classification algorithm')
parser.add_argument('--grid_search_cv', action="store_true",
                    help="set the GridSearchCV usage to select the best parameters (Default: False)")
parser.add_argument('--all_metrics', action="store_true",
                    help="displays all model metrics")
parser.add_argument('--classification_report', action="store_true",
                    help="display the classification_report metric")
parser.add_argument('--accuracy_score', action="store_true",
                    help="display the accuracy_score metric")
parser.add_argument('--confusion_matrix', action="store_true",
                    help="display the confusion_matrix metric")
parser.add_argument('--best_params', action="store_true",
                    help="display the GridSearchCV best parameters")
parser.add_argument('--learning_curve', action="store_true",
                    help="plot the learning_curve")
parser.add_argument('--roc_curve', action="store_true",
                    help="plot the roc_curve")
parser.add_argument('--precision_recall_curve', action="store_true",
                    help="plot the precision_recall_curve")
parser.add_argument('--export', action="store_true",
                    help="export the decision tree. Only valid for the <DecisionTrees> algorithm")

args = parser.parse_args()

if args.algorithm:
    model = algorithms[args.algorithm](args.grid_search_cv)
    model.train()
    model.predict()

    if args.all_metrics:
        model.metrics.show_all()
    else:
        if args.classification_report:
            model.metrics.classification_report()

        if args.accuracy_score:
            model.metrics.accuracy_score()

        if args.confusion_matrix:
            model.metrics.confusion_matrix()

        if args.learning_curve:
            model.metrics.learning_curve()

        if args.roc_curve:
            model.metrics.roc_curve()

        if args.precision_recall_curve:
            model.metrics.precision_recall_curve()

    if args.export:
        model.metrics.export()

    if args.best_params:
        print(model.get_best_param())
