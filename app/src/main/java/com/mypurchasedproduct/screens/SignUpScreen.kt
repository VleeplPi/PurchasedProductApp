package com.mypurchasedproduct.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mypurchasedproduct.R
import com.mypurchasedproduct.navigation.PurchasedProductAppRouter
import com.mypurchasedproduct.navigation.Screen
import com.mypurchasedproduct.ui.components.ButtonComponent
import com.mypurchasedproduct.ui.components.CheckBoxComponent
import com.mypurchasedproduct.ui.components.ClickableTextComponent
import com.mypurchasedproduct.ui.components.ClickableTextLogInComponent
import com.mypurchasedproduct.ui.components.DeviderTextComponent
import com.mypurchasedproduct.ui.components.HeadingTextComponent
import com.mypurchasedproduct.ui.components.MyTextField
import com.mypurchasedproduct.ui.components.MyTextFieldPassword
import com.mypurchasedproduct.ui.components.NormalTextComponent
import com.mypurchasedproduct.ui.theme.BackgroundColor
import com.mypurchasedproduct.ui.theme.SecondaryColor
import com.mypurchasedproduct.ui.theme.componentShapes


@Composable
fun SignUpScreen() {
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)

    ) {
        Column(
            modifier=Modifier.fillMaxSize(),
        ){

            NormalTextComponent(value= stringResource(id= R.string.hello))
            HeadingTextComponent(value = stringResource(id = R.string.create_account))
        }
        Column(
            modifier=Modifier.fillMaxSize(),
            verticalArrangement= Arrangement.Center
            ){
            Card(
                modifier= Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                border= BorderStroke(1.dp, SecondaryColor),
                shape = componentShapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,

                ),
            ){
                Column(modifier= Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                    verticalArrangement= Arrangement.Center){
                    MyTextField(labelValue = "Имя пользователя", painterResource(id = R.drawable.user_icon), KeyboardOptions(imeAction = ImeAction.Next))
                    Spacer(modifier=Modifier.height(10.dp))
                    MyTextFieldPassword(labelValue = "Пароль", painterResource(id = R.drawable.password_icon), KeyboardOptions(keyboardType= KeyboardType.Password, imeAction = ImeAction.Go))
                    CheckBoxComponent(stringResource(id = R.string.term_and_conditions), onTextSelected={
                        PurchasedProductAppRouter.navigateTo(Screen.TermsAndConditionsScreen)
                    } )
                    Spacer(modifier=Modifier.height(20.dp))
                    ButtonComponent(stringResource(R.string.signup_btn_text))
                    Spacer(modifier=Modifier.height(20.dp))
                    DeviderTextComponent("или")
                    ClickableTextLogInComponent(
                        onTextSelected = { PurchasedProductAppRouter.navigateTo(Screen.LogInScreen) })
                }

            }

        }

    }
}

@Preview
@Composable
fun DefaultPreviewOfSignUpScreen(){
    SignUpScreen();
}