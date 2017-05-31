# Track My Jog
[![Download APK](https://img.shields.io/badge/download-apk-green.svg)](https://drive.google.com/file/d/0B-byDzRxKccDbDNtWV91V2ptWVE/view?usp=sharing)

<div style="text-align: center;">
  <img style="width: 200px;" src ="http://i.imgur.com/VLydx5F.png" />
</div>

> A simple Android application to track jogging times of users

---

## Functionality

The application allows users to create their account and log in. When logged in, a user can see,
edit and delete his times he entered. Each time entry has a date, distance and time.

Filters by dates are allowed in the list of time entries, user can select a starting date,
finish date or both.

User is able to fetch a weekly report that will tell how many of his entries are reported on the
desired week. It shows information about **Total distance**, **Total duration** and
**Average speed**.

#### Roles

A **Regular user** is a common application user, he can create, retrieve, update and delete his
owned records. He is not able to see other users' records.

A **Manager user** has same permissions than regular, but additionally he is able to manage other
users, being able to create, retrieve, update and delete users.

An **Admin user** has same permissions than manager, but additionally he is able to manage other
users' records, that is, he can create, retrieve, update and delete other users' records.

## How is it built

_Track My Jog_ is built using great development tools, techniques and practices that are considered
clean.

The _Android_ application is built using the **Android SDK** version 25, with backwards
compatibility down to devices running _Android KitKat (19)_. **Clean Architecture** and
**Model-view-presenter (MVP)** were used in the project.

The back end application is built using the **Ruby on Rails** version 5. It is a RESTful API server
that exposes several endpoints to be consumed by the _Android_ or further clients.

## Screenshots
| Welcome page | Records |
|:---:|:---:|
| ![Welcome](http://i.imgur.com/0HmZYFq.jpg)  | ![Records](http://i.imgur.com/ypAEuHf.png) |

| Navigation drawer | Users |
|:---:|:---:|
| ![Navigation Drawer](http://i.imgur.com/0gPPlYx.png)  | ![Users](http://i.imgur.com/ooyRHtA.png) |

## Try it yourself

You can either download the APK file to test with demo server or run it locally.

To run it locally you will need to setup the back end first, then run the _Android_ application.
See the **back end README** to configure the server and the **mobile README** to launch the
_Android_ app.

[Ruby on Rails]:http://rubyonrails.org/
