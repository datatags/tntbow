# tntbow
A "fork" of the orignal tntbow plugin, which can be found at https://dev.bukkit.org/projects/tntbow

It is GPLv3 licensed according to that page, so this "fork" is as well.

The main changes are:
- Set api-version: 1.13
- Use standard recipe constructor instead of deprecated one
- Remove a bunch of unused variables
- Change /tntbow command to give a tntbow instead of displaying plugin info
- Setting ammoOn no longer affects creative mode players
- Get bow from EntityShootBowEvent instead of getting from player's hand, allowing tntbow to also work in offhand
