import argparse
from src.Regression.SVR import SVR
from src.Regression.KNeighbors import KNeighbors
from src.Regression.DecisionTrees import DecisionTree
from src.Regression.RandomForest import RandomForest

algorithms = {'SVR': SVR,
              'KNeighbors': KNeighbors,
              'DecisionTree': DecisionTree,
              'RandomForest': RandomForest,
              }

parser = argparse.ArgumentParser(description='Supervised Learning - Regression')
parser.add_argument('algorithm', choices=['SVR', 'DecisionTree', 'KNeighbors', 'RandomForest'],
                    help='set the supervised learning regression algorithm')
parser.add_argument('--grid_search_cv', action="store_true",
                    help="set the GridSearchCV usage to select the best parameters (Default: False)")
parser.add_argument('--all_metrics', action="store_true",
                    help="displays all model metrics")
parser.add_argument('--explained_variance_score', action="store_true",
                    help="display the explained_variance_score metric")
parser.add_argument('--max_error', action="store_true",
                    help="display the max_error metric")
parser.add_argument('--mean_absolute_error', action="store_true",
                    help="display the mean_absolute_error metric")
parser.add_argument('--mean_squared_error', action="store_true",
                    help="display the mean_squared_error metric")
parser.add_argument('--median_absolute_error', action="store_true",
                    help="display the median_absolute_error metric")
parser.add_argument('--r2_score', action="store_true",
                    help="display the r2_score metric")
parser.add_argument('--mean_poisson_deviance', action="store_true",
                    help="display the mean_poisson_deviance metric")
parser.add_argument('--mean_gamma_deviance', action="store_true",
                    help="display the mean_gamma_deviance metric")
parser.add_argument('--feat_importance', action="store_true",
                    help="display the feature importance")
parser.add_argument('--best_params', action="store_true",
                    help="display the GridSearchCV best parameters")
parser.add_argument('--learning_curve', action="store_true",
                    help="plot the learning_curve")
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
        if args.explained_variance_score:
            model.metrics.explained_variance_score()

        if args.max_error:
            model.metrics.max_error()

        if args.mean_absolute_error:
            model.metrics.mean_absolute_error()

        if args.mean_squared_error:
            model.metrics.mean_squared_error()

        if args.median_absolute_error:
            model.metrics.median_absolute_error()

        if args.r2_score:
            model.metrics.r2_score()

        if args.mean_poisson_deviance:
            model.metrics.mean_poisson_deviance()

        if args.mean_gamma_deviance:
            model.metrics.mean_gamma_deviance()

        if args.feat_importance:
            model.metrics.feature_importance()

        if args.learning_curve:
            model.metrics.learning_curve()

    if args.export:
        model.metrics.export()

    if args.best_params:
        print(model.get_best_param())
