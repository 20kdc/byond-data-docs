/turf/white_tile
	text = ":"

/turf/grey_tile
	text = "."
	var/interesting = 1

/turf/black_tile
	text = " "

/mob
	text = "@"
	parent_type = /atom/movable

/mob/reversi

/mob/reversi/black
	text = "o"

/mob/reversi/white
	text = "O"

// Maps can only be included via #include - directly doesn't work.

#include "chessboard.dmm"

