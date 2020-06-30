---
description: >-
  You have learned about the Paths system, but now you want to build your own
  paths. This page explains how to do that.
---

# Building your own Paths

## The structure of a path

We've learned about the Paths system on the previous pages and now it is time to build our own path! To do that, you'll need to go to the **Paths.yml** file in the Autorank plugin folder.

The basic setup of a path looks like this:

```yaml
Name of the path:    # Name of the path
    prerequisites:                    # The prerequisite section.
        some prerequisite:            
            value: value specific to the prerequisite
        some prerequisite 2:          
            value: value specific to the prerequisite
    requirements:                     # The requirements section.
        some requirement name:        
            value: values specific to the requirement
        some requirement name 2:      
            value: values specific to the requirement
    results:                          # The results section.          
        some result: 
            value: value specific to the result
        some result 2: 
            value: value specific to the result
```

{% hint style="info" %}
It is important to carefully check your indentation \(spaces and tabs\)! Each section of a path has a **level of indentation**. 
{% endhint %}

You can see that there are basically three important subsections of a path. You start off with the name of the path at the top, and then get to the three subsections:

* **Prerequisites**
  * Conditions that a player needs to meet to be eligible for the path.
* **Requirements**
  * Conditions that the player needs to fulfill to complete the path. 
* **Results**
  * Actions that are performed after the player completes a path.

Note that these subsections are all located at the same level of indentation! It is very important that this is always the case because it's a specification of the YAML standard.

## Your first path

Now that we know the structure of a path, can you decipher the path below?

```yaml
Running a marathon:
    prerequisites:
        in group: 
            value: 'Participants'
    requirements:
        blocks moved: 
            value: 1000
    results:
        money: 
            value: 10000
```

Try to figure out what this path means, it should be familiar \(if you've read the previous pages\).

#### Answer

This path is called _Running a marathon_ and requires players to move at least a thousand blocks to complete it. To be able to activate this path, players need to be in the group _Participants_. Once the player has completed the \(only\) requirement of this path, the path is completed. Upon completion of the path, the player will receive ten thousand money \(in whatever currency unit you use\).

