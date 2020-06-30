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
            value: 1000;0
    results:
        money: 
            value: 10000
```

Try to figure out what this path means, it should be familiar \(if you've read the previous pages\).

#### Answer

This path is called _Running a marathon_ and requires players to move at least a thousand blocks to complete it \(by walking\). The player will need to walk as opposed to flying, swimming, or riding in a minecart. This is indicated by the zero behind the '1000', but don't worry about that for now.

To be able to activate this path, players need to be in the group _Participants_. Once the player has completed the \(only\) requirement of this path, the path is completed. Upon completion of the path, the player will receive ten thousand money \(in whatever currency unit you use\).

{% hint style="warning" %}
Note that Autorank will only check whether a player has completed a requirement if the path is **active!** When the player walks for a thousand blocks but has not activated the path, it will never receive the money.
{% endhint %}

## Building your own path

Hopefully, you've got the hang of it now. One of the most frequent use-cases of Autorank is allowing players to gain ranks by playing on the server for a while. Due to popular request, we can work out an example here.

### **Ranking up based on play-time**

Let's say we have the following ranks on our server:

* Default \(new players start in this rank\)
* Newbie
* Member
* Trusted Member
* Elite

Now let's say we want players to be able to rank up to a new rank after the following times:

* Default \(new players start in this rank\)
* Newbie \(after playing for 2 hours\)
* Member \(after playing for one day\)
* Trusted Member \(after playing for a week\)
* Elite \(after playing for a year\)

With your knowledge of paths, try to set it up using the path system! The answer is given below.

{% hint style="success" %}
The **&p** tag is used to indicate the player's name. Whenever it appears, Autorank will replace it with the corresponding name of the player.
{% endhint %}

{% tabs %}
{% tab title="Newbie path" %}
```yaml
Newbie Path:
    prerequisites:
        in group: 
            value: 'Default' # Player is in the Default path to activate this path
    requirements:
        time: 
            value: '2h'
    results:
        command: 
            value: 'lp user &p parent set Newbie' # Set the player to Newbie rank
```
{% endtab %}

{% tab title="Member path" %}
```yaml
Member Path:
    prerequisites:
        in group: 
            value: 'Newbie'
    requirements:
        time: 
            value: '1d'
    results:
        command: 
            value: 'lp user &p parent set Member' # Set the player to Member rank
```
{% endtab %}

{% tab title="Trusted Member path" %}
```yaml
Trusted Member Path:
    prerequisites:
        in group: 
            value: 'Member'
    requirements:
        time: 
            value: '7d' # 7 days is a week!
    results:
        command: 
            value: 'lp user &p parent set Trusted Member'
```
{% endtab %}

{% tab title="Elite path" %}
```yaml
Elite Path:
    prerequisites:
        in group: 
            value: 'Trusted Member'
    requirements:
        time: 
            value: '365d' # 365 days is a year!
    results:
        command: 
            value: 'lp user &p parent set Elite'
```
{% endtab %}
{% endtabs %}

{% hint style="info" %}
You can also combine time units in the **time** requirement. When you define '1d 1h', it's the same as '25h'.
{% endhint %}

When you're finished, the Paths file should look like this:

```yaml
Newbie Path:
    prerequisites:
        in group: 
            value: 'Default' # Player is in the Default path to activate this path
    requirements:
        time: 
            value: '2h'
    results:
        command: 
            value: 'lp user &p parent set Newbie' # Set the player to Newbie rank

Member Path:
    prerequisites:
        in group: 
            value: 'Newbie'
    requirements:
        time: 
            value: '1d'
    results:
        command: 
            value: 'lp user &p parent set Member' # Set the player to Member rank

Trusted Member Path:
    prerequisites:
        in group: 
            value: 'Member'
    requirements:
        time: 
            value: '7d' # 7 days is a week!
    results:
        command: 
            value: 'lp user &p parent set Trusted Member'

Elite Path:
    prerequisites:
        in group: 
            value: 'Trusted Member'
    requirements:
        time: 
            value: '365d' # 365 days is a year!
    results:
        command: 
            value: 'lp user &p parent set Elite'
```

{% hint style="danger" %}
If you're having trouble with Autorank recognizing your Paths file, make sure it is properly formatted. You can use [this tool](https://onlineyamltools.com/validate-yaml) to check whether your syntax is correct.
{% endhint %}

## Improvements to your paths

Now that we've built our first path, we might want to improve it a bit. Let's look into a few improvements!

### Multiple requirements

Often you want players to fulfill more than one requirement to be able to complete a path. Let's take the example given above. You want a player to go from the Default rank to the Newbie rank when he's played for 2 hours **and** he's obtained at least 100 money. It's very easy to add additional requirements, see the example below.

```yaml
Newbie Path:
    prerequisites:
        in group: 
            value: 'Default'
    requirements:         # There are two requirements in this section
        time: 
            value: '2h'
        money:
            value: 100
    results:
        command: 
            value: 'lp user &p parent set Newbie' 
```

### Multiple prerequisites

Just like requirements, you can also use multiple prerequisites. Let's say we want a player to be in two groups at the same time before he is eligible for a path.

```yaml
Path with two prerequisites:
    prerequisites:    # Player should be in the Default and Newbie group!
        in group: 
            value: 'Default'
        in group2: 
            value: 'Newbie'
# Requirements and results are left out for brevity
```

{% hint style="warning" %}
Whenever you want to use multiple of the same requirement \(or prerequisite\) you should give them a **unique name**, but still have the _type name._ The easiest way is to add a number at the end of the name, as Autorank will automatically remove the numbers. See the example above \(both prerequisites are of the type _in group_\).
{% endhint %}

### Run a result when activating a path

Let's say you want to give the player something when they start a path; perhaps the path requires the player to walk a thousand blocks, so you surely want to give them some food before they embark on their long journey. To run results whenever a player activates a path, you'll need to add a new subsection to your path. See the example below.

```yaml
Travel across the world:
    prerequisites:
        in group:  # Only travellers can undertake such a long journey.
            value: Travellers
    upon choosing: # New section that performs results when the path is activated
        command: 
            value: "give &p bread 5" # Give the player some bread
        message: 
            value: "Good luck on your journey."
    requirements:
        blocks moved: 
            value: 1000;0
    results:
        message:   # Tell the player they've done well.
            value: "You've become a true traveller!"
```

{% hint style="info" %}
The _upon choosing_ section can use the same results at the _results_ section; Autorank is smart enough to understand that these are just results performed at an earlier stage of the path.
{% endhint %}

The path will perform the results that are specified in the upon choosing section. Note that **the indentation matches that of the other sections**!

### Set a different display name for a path

Whenever a player activates a path or checks the progress on their path, they will have to use name of the path as specified in your Paths.yml file. If you want to show a different name, you should not adjust the name at the top of your paths declaration! If you do, Autorank will think that it is a completely new path \(and will kick players out of their current path\). Instead, you should add a display name to the path, like so:

```yaml
Very long and ugly path name: # DO NOT change this name
    prerequisites:
        # Some prerequisites here
    requirements:
        # Some requirements here
    results:
        # Some results here
    options:
        display name: "New path name" # Instead, add this to your path
```

The _options_ section is used to configure additional options for a path. Note that it has the same indentation as the other subsections!

