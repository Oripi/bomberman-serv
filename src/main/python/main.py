#!/usr/bin/env python3

###
# #%L
# Codenjoy - it's a dojo-like platform from developers to developers.
# %%
# Copyright (C) 2018 Codenjoy
# %%
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public
# License along with this program.  If not, see
# <http://www.gnu.org/licenses/gpl-3.0.html>.
# #L%
###


from sys import version_info
from webclient import WebClient
from dds import DirectionSolver
from urllib.parse import urlparse, parse_qs
from model.data_generator import start
from model.nn_model import freeze_graph


def get_url_for_ws(url):
    parsed_url = urlparse(url)
    query = parse_qs(parsed_url.query)
    # host = "3.133.109.198"
    # port = "8080"
    # playerId = "g2kzb99qhnjs217fkcyy"
    # code = "3689161262388400247"
    # "http://" + host + ":" + port + "/codenjoy-contest/board/player/" + playerId + "?code=" + code,

    return "{}://{}/codenjoy-contest/ws?user={}&code={}".format('ws' if parsed_url.scheme == 'http' else 'wss',
                                                                parsed_url.netloc,
                                                                parsed_url.path.split('/')[-1],
                                                                query['code'][0])


def main():
    assert version_info[0] == 3, "You should run me with Python 3.x"

    # substitute following link with the one you've copied in your browser after registration
    url = "http://3.133.109.198:8080//codenjoy-contest/board/player/g2kzb99qhnjs217fkcyy?code=3689161262388400247&gameName=bomberman"
    direction_solver = DirectionSolver()

    print(get_url_for_ws(url))
    wcl = WebClient(url=get_url_for_ws(url), solver=direction_solver)
    wcl.run_forever()


if __name__ == '__main__':
    # main()
    model, labels = start()
    model.save('./export/my_model')
    freeze_graph('./export')
