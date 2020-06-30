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

{% tabs %}
{% tab title="Newbie path" %}
```yaml
Newbie Path:
    prerequisites:
        in group: 
            value: 'Default' # Players should be in the Default path to activate this path
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

