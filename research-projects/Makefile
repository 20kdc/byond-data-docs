OBJECTS = \
 proc-inherit-quirks-1.dmb proc-inherit-quirks-2.dmb proc-inherit-quirks-3.dmb \
 rand-inherit.dmb turf-atom-equivalence.dmb savefile.dmb world-channel.dmb \
 modified-types.dmb a-very-long-output.dmb mapsurgery-game.dmb mapsurgery-map.dmb \
 world-oddprops.dmb

all: $(OBJECTS)
.PHONY: clean
clean:
	rm -f $(OBJECTS)

%.dmb: %.dme
	DreamMaker $<
