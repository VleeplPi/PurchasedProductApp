package com.mypurchasedproduct.presentation.screens


import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mypurchasedproduct.R
import com.mypurchasedproduct.presentation.ViewModel.AddProductViewModel
import com.mypurchasedproduct.presentation.ViewModel.AddPurchasedProductFormViewModel
import com.mypurchasedproduct.presentation.ViewModel.AuthViewModel
import com.mypurchasedproduct.presentation.ViewModel.CategoryViewModel
import com.mypurchasedproduct.presentation.ViewModel.DateRowListViewModel
import com.mypurchasedproduct.presentation.ViewModel.EditPurchasedProductFormViewModel
import com.mypurchasedproduct.presentation.ViewModel.HomeViewModel
import com.mypurchasedproduct.presentation.ViewModel.MeasurementUnitsListViewModel
import com.mypurchasedproduct.presentation.ViewModel.ProductListBottomSheetViewModel
import com.mypurchasedproduct.presentation.ViewModel.PurchasedProductListViewModel
import com.mypurchasedproduct.presentation.navigation.PurchasedProductAppRouter
import com.mypurchasedproduct.presentation.navigation.Screen
import com.mypurchasedproduct.presentation.ui.components.AddCategoryForm
import com.mypurchasedproduct.presentation.ui.components.AddPurchasedProductFormComponent
import com.mypurchasedproduct.presentation.ui.components.AlertDialogComponent
import com.mypurchasedproduct.presentation.ui.components.DaysRowComponent
import com.mypurchasedproduct.presentation.ui.components.DialogCardComponent
import com.mypurchasedproduct.presentation.ui.components.EditPurchasedProductFormComponent
import com.mypurchasedproduct.presentation.ui.components.ErrorMessageDialog
import com.mypurchasedproduct.presentation.ui.components.FormModalBottomSheet
import com.mypurchasedproduct.presentation.ui.components.HeadingTextComponent
import com.mypurchasedproduct.presentation.ui.components.LoadScreen
import com.mypurchasedproduct.presentation.ui.components.MyTextField
import com.mypurchasedproduct.presentation.ui.components.NormalTextComponent
import com.mypurchasedproduct.presentation.ui.components.PrimaryFloatingActionButton
import com.mypurchasedproduct.presentation.ui.components.PrimaryGradientButtonComponent
import com.mypurchasedproduct.presentation.ui.components.PurchasedProductViewComponent
import com.mypurchasedproduct.presentation.ui.components.SelectCategoryButton
import com.mypurchasedproduct.presentation.ui.components.SuccessMessageDialog
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    appRouter: PurchasedProductAppRouter = PurchasedProductAppRouter,
    homeViewModel: HomeViewModel = viewModel(),
    dateRowListViewModel: DateRowListViewModel = viewModel(),
    addPurchasedProductFormViewModel: AddPurchasedProductFormViewModel = viewModel(),
    addProductViewModel: AddProductViewModel = viewModel(),
    categoryVM: CategoryViewModel = viewModel(),
    purchasedProductListVM: PurchasedProductListViewModel = viewModel(),
    productListBottomSheetVM: ProductListBottomSheetViewModel = viewModel(),
    measurementUnitsListVM: MeasurementUnitsListViewModel = viewModel(),
    editPurchasedProductFormVM: EditPurchasedProductFormViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val homeState = homeViewModel.state.collectAsState()
    val authState = authViewModel.state.collectAsState()
    LaunchedEffect(authState.value.isSignIn){
        Log.d("HomeScreen.LaunchedEffect", "AUTH STATE")
        if(!authState.value.isSignIn){
            appRouter.navigateTo(Screen.AuthScreen)
        }
    }
    val dateRowState = dateRowListViewModel.state.collectAsState()
    LaunchedEffect(dateRowState.value.selectedDate){
        val mills = dateRowListViewModel.getSelectedDayTimestamp()
        Log.wtf("HomeScreen.LaunchedEffect", "selected date: ${mills}\t")
        purchasedProductListVM.getPurchasedProductCurrentUserByDate(mills)
    }

    val rememberCoroutineScope = rememberCoroutineScope()

    val totalCosts = purchasedProductListVM.totalCosts.collectAsState()
    LoadScreen(isActive=homeState.value.isLoading)
    val msgState = homeViewModel.msgState.collectAsState()
    if(msgState.value.isSuccess){
        SuccessMessageDialog(
            text = msgState.value.header,
            onDismiss = {
                scope.launch{
                    msgState.value.onConfirm()
                    homeViewModel.setDefaultMsgState()
                }
            }
        )
    }
    if(msgState.value.isError){
        ErrorMessageDialog(
            headerText = msgState.value.header,
            description = msgState.value.description,
            onDismiss = {
                homeViewModel.setDefaultMsgState()
                addPurchasedProductFormViewModel.setDefaultState()
            })
    }
    val deletePurchasedProductState = purchasedProductListVM.deletePurchasedProductState.collectAsState()
    if(deletePurchasedProductState.value.isActive){
        AlertDialogComponent(
            headerText="Удалить купленный продукт?",
            onDismiss = {purchasedProductListVM.onDismissDeletePurchasedProduct()},
            onConfirm = {purchasedProductListVM.deletePurchasedProduct()},
        )
        {
            NormalTextComponent(value = "Будет удалено: ${deletePurchasedProductState.value?.purchasedProduct?.product?.name}")
        }
        if(deletePurchasedProductState.value.isSuccess){
            SuccessMessageDialog(
                text = "Купленный продукт удален!",
                onDismiss = {
                    purchasedProductListVM.setDefaultDeletePurchasedProductState()
                    homeViewModel.setLoadingState(false)
                }
            )
        }
        if(deletePurchasedProductState.value.isError){
            ErrorMessageDialog(
                headerText = "Что-то пошло не так",
                description = deletePurchasedProductState.value.error.toString(),
                onDismiss = {
                    purchasedProductListVM.setDefaultDeletePurchasedProductState()
                    homeViewModel.setLoadingState(false)
                }
            )
        }
    }
    Scaffold(
        topBar = {
            Column {
                DaysRowComponent(dateRowListViewModel)
                HeadingTextComponent(value = "Потрачено сегодня: ${totalCosts.value} ₽")
            }
        },
        content = {paddingValues: PaddingValues ->
            val addPurchasedProductState = addPurchasedProductFormViewModel.state.collectAsState()
            PurchasedProductViewComponent(
                purchasedProductListVM,
                paddingValues=paddingValues,
                onSwipeToEdit = {
                    editPurchasedProductFormVM.setActive(true)
                    editPurchasedProductFormVM.setPurchasedProduct(it)
                }
            )
            FormModalBottomSheet(
                openBottomSheet = addPurchasedProductState.value.isActive,
                setStateBottomSheet = {
                    scope.launch {
                        addPurchasedProductFormViewModel.setActiveAddPurchasedProductForm(it)
                    }

                }
            )
            {
                AddPurchasedProductFormComponent(
                    addPurchasedProductVM = addPurchasedProductFormViewModel,
                    productListBottomSheetVM = productListBottomSheetVM,
                    measurementUnitsListVM = measurementUnitsListVM,
                    onClickAddProduct = {
                        addProductViewModel.onClickAddProduct()
                        addProductViewModel.findCategories()
                    },
                    onConfirm = {
                        scope.launch{
                            purchasedProductListVM.toAddPurchasedProduct(
                                it,
                                dateRowListViewModel.getSelectedDayTimestamp(),
                                onSuccess = {
                                    homeViewModel.setSuccessMsgState(
                                        header=it,
                                        onConfirm = { addPurchasedProductFormViewModel.setDefaultState() }
                                    )})
                        }

                    },
                    onDismiss = {addPurchasedProductFormViewModel.setActiveAddPurchasedProductForm(false)}
                )
            }

        },
        floatingActionButton = {
            PrimaryFloatingActionButton(
                painter = painterResource(id = R.drawable.ic_plus),
                onClick={
                    Log.wtf("FLOATIONG BUTTON","ON CLICK FLOATIG BUTTTON: [start]")
                    rememberCoroutineScope.launch {
                        addPurchasedProductFormViewModel.setActiveAddPurchasedProductForm(true)

                    }
                    Log.wtf("FLOATIONG BUTTON","ON CLICK FLOATIG BUTTTON: [END]")
                },

                )
        },
        bottomBar = {
            PrimaryGradientButtonComponent(
                value = "Выйти", onClickButton = {
                    authViewModel.signOut()
                    appRouter.navigateTo(Screen.AuthScreen)
                }
            )
        }
    )
    Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            val deletePurchasedProductState = purchasedProductListVM.deletePurchasedProductState.collectAsState()
            val editPurchasedProductState = editPurchasedProductFormVM.state.collectAsState()

//            TODO("EDIT PURCHASED PRODUCT")
        FormModalBottomSheet(
            openBottomSheet = editPurchasedProductState.value.isActive,
            setStateBottomSheet = {
                editPurchasedProductFormVM.setActive(it)
            },
            onDismissRequest = {
                editPurchasedProductFormVM.setDefaultState()
                editPurchasedProductFormVM.clearErrors()
            }
        ){
            EditPurchasedProductFormComponent(
                editPurchasedProductVM = editPurchasedProductFormVM,
                productListBottomSheetVM = productListBottomSheetVM,
                measurementUnitsListVM = measurementUnitsListVM,
                onConfirm = {},
                onClickAddProduct = {},
                onDismiss = {
                    editPurchasedProductFormVM.setDefaultState()
                    editPurchasedProductFormVM.clearErrors()
                }
            )
        }

//            if(editPurchasedProductState.isActive){
//                FormModalBottomSheet(
//                    openBottomSheet = editPurchasedProductState.isActive,
//                    setStateButtomSheet = {
//                        purchasedProductListVM.setActiveEditPurchasedProduct(it)
//                    }
//                )
//                {
//                    if (editPurchasedProductState.purchasedProduct != null) {
//                        EditPurchasedProductFormComponent(
//                            products = addProductViewModel.getProductsState.products,
//                            measurementUnits = measurementUnits,
//                            onClickAddProduct = {
//                                addProductViewModel.onClickAddProduct()
//                                addProductViewModel.findCategories()
//                            },
//                            onConfirm = {
//                                purchasedProductListVM.toEditPurchasedProduct(it)
//                                        },
//                            onDismiss = {
//                                purchasedProductListVM.setActiveEditPurchasedProduct(false)
//                                purchasedProductListVM.setDefaultEditPurchasedProductState()
//                            },
//                            purchasedProduct = editPurchasedProductState.purchasedProduct
//                        )
//                    }
//                }
//
//            }

            if(editPurchasedProductState.value.isSuccess){
                SuccessMessageDialog(
                    text ="купленный продукт изменен",
                    onDismiss = {
                        purchasedProductListVM.setDefaultEditPurchasedProductState()
//                        purchasedProductListVM.setActiveEditPurchasedProduct(false)
                    })
            }
//            if(editPurchasedProductState.value.isError){
//                ErrorMessageDialog(
//                    headerText ="Что-то пошло не так" ,
//                    description = editPurchasedProductState.value.error.toString(),
//                    onDismiss = {
////                        purchasedProductListVM.setActiveEditPurchasedProduct(false)
//                        purchasedProductListVM.setDefaultEditPurchasedProductState()
//
//                    }
//                )
//            }


            if(categoryVM.addCategoryState.isActive){
                val addCategoryState = categoryVM.addCategoryState
                AddCategoryForm(
                    isLoading=addCategoryState.isLoading,
                    onConfirm = {categoryVM.addCategory(it)},
                    onDismiss = {categoryVM.setActiveAddCategory(it)})
                if(addCategoryState.isSuccess){
                    SuccessMessageDialog(
                        text = "категория добавлена!",
                        onDismiss = {
                            addProductViewModel.findCategories()
                            categoryVM.setActiveAddCategory(false)
                            categoryVM.setDefaultState()
                        })
                }
                if(addCategoryState.isError){
                    ErrorMessageDialog(
                        headerText = "Что-то пошло не так",
                        description = addCategoryState.error.toString(),
                        onDismiss = {
                            categoryVM.setActiveAddCategory(false)
                            categoryVM.setDefaultState()
                        })
                }
            }
            if (addProductViewModel.addProductFormState.isActive) {
                val getCategoriesState = addProductViewModel.getCategoriesState
//                        val productItem = addProductViewModel.productItem
                var addedProductName by remember {
                    mutableStateOf("")
                }
                val addProductState = addProductViewModel.addProductState
                DialogCardComponent(
                    onDismiss = { addProductViewModel.onClickCloseAddProduct() },
                    onConfirm = { addProductViewModel.toAddProduct(addedProductName) }
                ) {
                    if(addProductViewModel.getCategoriesState.isSuccess) {
                        val categories = getCategoriesState.categories
                        if(categories != null){
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween
                            ){
                                LazyRow(
                                    modifier = Modifier.fillMaxWidth(.85f),
                                    userScrollEnabled = true
                                ) {
                                    items(categories){categoryResponse ->
                                        SelectCategoryButton(
                                            categoryResponse = categoryResponse,
                                            onClick = {
                                                addProductViewModel.setProductCategoryId(
                                                    categoryResponse.id
                                                )
                                            }
                                        )
                                    }
                                }
                                IconButton(onClick = { categoryVM.setActiveAddCategory(true) }) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = null)
                                }
                            }
                        }
                        else{
                            LinearProgressIndicator()
                        }
                        MyTextField(
                            textValue = addedProductName,
                            labelValue = "продукт",
                            onValueChange = {
                                addedProductName = it
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                    }
                    if (addProductViewModel.addProductState.isError) {
                        ErrorMessageDialog(
                            headerText = "Что-то пошло не так",
                            description = addProductViewModel.addProductState.error.toString(),
                            onDismiss = {addProductViewModel.setDefaultAddProductState()}
                        )
                    }
                    if (addProductState.isSuccess) {
                        SuccessMessageDialog(
                            text = "Продукт добавлен!",
                            onDismiss = {
                                addProductViewModel.setDefaultProductItem()
                                addProductViewModel.setDefaultAddProductState()
                                addProductViewModel.findProducts()
                            }
                        )
                    }
                }
            }


        }

}
