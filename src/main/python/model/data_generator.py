import sys
# insert at 1, 0 is the script path (or '' in REPL)
# sys.path.append('/')
from random import choice
from direction import DirectionOptions, Direction
import numpy as np
from model.nn_model import create_dummy_model
import tensorflow as tf

LR = 1e-3
goal_steps = 300
score_requirement = 50
initial_games = 5000

class MockEnv():
    def step(self, action):
        return list('☼☼☼☼☼#     ♥   #♥♥ ##   &     ###   ☼☼ ☼#☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼ ☼#☼ ☼ ☼☼#    #  #  # # # #           # ☼☼ ☼ ☼♥☼ ☼ ☼ ☼ ☼ ☼ ')

def generate_population(model, env):
    # [OBS, MOVES]
    global score_requirement
 
    training_data = []
    # all scores:
    scores = []
    # just the scores that met our threshold:
    accepted_scores = []
    # iterate through however many games we want:
    print('Score Requirement:', score_requirement)
    for _ in range(initial_games):
        print('Simulation ', _, " out of ", str(initial_games), '\r', end='')
        # reset env to play again
        # env.reset()
 
        score = 0
        # moves specifically from this environment:
        game_memory = []
        # previous observation that we saw
        prev_observation = None
        # for each frame in 200
        for _ in range(goal_steps):
            # choose random action (0 or 1)
            if len(prev_observation) == 0:
                random_direction = choice(list(DirectionOptions))
                action = Direction(random_direction.value)
                # action = random.randrange(0, 3)
            else:
                if not model:
                    random_direction = choice(list(DirectionOptions))
                    action = Direction(random_direction.name)
                    # action = random.randrange(0, 3)
                else:
                    prediction = model.predict(prev_observation.reshape(-1, len(prev_observation), 1))
                    action = np.argmax(prediction[0])
 
            # do it!
            observation, reward, done, info = env.step(action)
 
            # notice that the observation is returned FROM the action
            # so we'll store the previous observation here, pairing
            # the prev observation to the action we'll take.
            if len(prev_observation) > 0:
                game_memory.append([prev_observation, action])
            prev_observation = observation
            score += reward
            if done: break
 
        # IF our score is higher than our threshold, we'd like to save
        # every move we made
        # NOTE the reinforcement methodology here.
        # all we're doing is reinforcing the score, we're not trying
        # to influence the machine in any way as to HOW that score is
        # reached.
        if score >= score_requirement:
            accepted_scores.append(score)
            for data in game_memory:
                # convert to one-hot (this is the output layer for our neural network)
 
                action_sample = [0, 0, 0]
                action_sample[data[1]] = 1
                output = action_sample
                # saving our training data
                training_data.append([data[0], output])
 
        # save overall scores
        scores.append(score)
 
    # some stats here, to further illustrate the neural network magic!
    print('Average accepted score:', mean(accepted_scores))
    print('Score Requirement:', score_requirement)
    print('Median score for accepted scores:', median(accepted_scores))
    print(Counter(accepted_scores))
    score_requirement = mean(accepted_scores)
 
    # just in case you wanted to reference later
    training_data_save = np.array([training_data, score_requirement])
    np.save('saved.npy', training_data_save)
 
    return training_data
 
def start():
    mock_env = MockEnv()
    labels = list(map(lambda x: x.name, list(DirectionOptions)))
    training_data = [[mock_env.step(action=None), list(map(lambda x: x.name, list(DirectionOptions)))]]
    model = create_dummy_model(training_data)
    del tf.get_collection_ref(tf.GraphKeys.TRAIN_OPS)[:]
    return model, labels

if __name__ == "__main__":
 
    # some_random_games_first()
    # initial_population
    mock_env = MockEnv()
    # training_data = generate_population(None, env=mock_env)
    # creating a dummy model
    model = create_dummy_model(training_data)
    print(model)