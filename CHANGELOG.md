## Changelog

This release contains a lot of balance, and polish features, but _**will not be backported to 1.18.2!**_ Don't ask!

### Features

- Add villager and wandering trader trades.
- Port to 1.19.
- The different tiers of reapers now have varying chances of killing the entity when used - to represent sharpness/the
  cleanness of the cut.
- Add a cooldown to the reaper based on its tier.
- Add tooltips to the config.
- Remove some unnecessarily granular configs.

### Fixes

- Fix bug with Cloth Config dependency on Forge and Fabric.
- Entities that are dying (i.e. playing the death animation) can no longer be reaped.
- Use some Architectury API features in place of custom code.
- General code clean-up to make this more maintainable.

Closed Issues: #4.

[Full Changelog](https://github.com/JamCoreModding/Reaping/compare/2.1.4...2.2.0)
