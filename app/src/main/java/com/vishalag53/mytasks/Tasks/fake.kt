//    @RequiresApi(Build.VERSION_CODES.O)
//    private fun createNotificationChannel(){
//        val name = "Notification Channel"
//        val desc = "A Description of the Channel"
//        val importance = NotificationManager.IMPORTANCE_DEFAULT
//        val channel = NotificationChannel(channelId,name,importance)
//        channel.description = desc
//        val notificationManager = requireContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//        notificationManager.createNotificationChannel(channel)
//    }
//
//    @RequiresApi(Build.VERSION_CODES.M)
//    private fun scheduleNotification(){
//        val intent = Intent(requireContext,Notification::class.java)
//        val title = title
//        val message = details
//        intent.putExtra(titleExtra,title)
//        intent.putExtra(messageExtra,message)
//
//        val pendingIntent = PendingIntent.getBroadcast(requireContext, notificationId,intent,PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val alarmManager = requireContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val time = getTimeForNotification()
//        try {
//            alarmManager.setExactAndAllowWhileIdle(
//                AlarmManager.RTC_WAKEUP,
//                time,
//                pendingIntent
//            )
//        } catch (e: SecurityException){
//
//        }
//        showAlert(time,title,message)
//    }
//
//    private fun showAlert(time: Long, title: String, message: String) {
//        val date = Date(time)
//        val dateFormat = android.text.format.DateFormat.getLongDateFormat(requireContext)
//        val timeFormat = android.text.format.DateFormat.getTimeFormat(requireContext)
//
//        AlertDialog.Builder(requireContext)
//            .setTitle("Notification Scheduled")
//            .setMessage(
//                "Title: " + title +
//                "\n Message: " + message +
//                "\n At: " + dateFormat.format(date) + " " + timeFormat.format(date)
//            )
//            .show()
//    }
//
//    private fun getTimeForNotification(): Long {
//        val textDate = date
//        val days = getDay(textDate)
//        val year = if (days in 0..9) getYear(textDate, false)
//        else getYear(textDate, true)
//        val month = getMonth(textDate)
//
//        val txtTime = time
//        var hours = getHours(txtTime)
//        val minute = if (hours in 0..9) getMinute(txtTime, false)
//        else getMinute(txtTime, true)
//        val isPM = if (hours in 0..9) getPM(txtTime, false)
//        else getPM(txtTime, true)
//
//        if (isPM) hours += 12
//
//        val calendar = Calendar.getInstance()
//        calendar.set(year,month,days,hours, minute)
//        return calendar.timeInMillis
//    }
//