# Machine learning - Classification/Regression

## Dependencies

- [Python 3.6](https://www.python.org/downloads/release/python-360/)
- [PyCharm](https://www.jetbrains.com/pycharm/) (_Optional_)

## Instructions

1. Install the required python interpreter (Python 3.6)

2. Install graphviz
    - `sudo apt install python-pydot python-pydot-ng graphviz`

3. Create a virtual environment `venv`

4. Access the virtual environment
    - `. venv/bin/activate`

5. Install the dependencies inside the `venv` the file `requirements.txt`
    - `pip3 install -r requirements.txt`
    
6. Run
    - Using PyCharm: 
        - `run classification.py or regression.py (Run -> Run 'target_file.py')`
    - Using console and inside venv
        - `python3.6 -W ignore -m src.[classification or regression]`

## Note

* Relative to the regression there are two different scenarios: predicting the hand-selection and predicting the aggression. In order to set which of this problems you want to solve you must do the following:
    - In `regression dataset` file **change** the `__init_ function` target parameter for your problem. Options:
        - `hand-selection`
        - `aggression` 